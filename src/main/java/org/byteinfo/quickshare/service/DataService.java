package org.byteinfo.quickshare.service;

import javafx.application.Platform;
import org.byteinfo.context.Context;
import org.byteinfo.logging.Log;
import org.byteinfo.quickshare.App;
import org.byteinfo.quickshare.config.Config;
import org.byteinfo.quickshare.controller.ChatController;
import org.byteinfo.quickshare.controller.ContactController;
import org.byteinfo.quickshare.controller.ContentController;
import org.byteinfo.quickshare.message.ControlMessage;
import org.byteinfo.quickshare.message.DataMessage;
import org.byteinfo.quickshare.message.TextMessage;
import org.byteinfo.quickshare.model.ContactModel;
import org.byteinfo.quickshare.model.DataMessageModel;
import org.byteinfo.quickshare.model.MessageModel;
import org.byteinfo.quickshare.model.TextMessageModel;
import org.byteinfo.quickshare.util.Constant;
import org.byteinfo.quickshare.util.Device;
import org.byteinfo.quickshare.util.JSON;
import org.byteinfo.quickshare.util.NetUtil;
import org.byteinfo.socket.Endpoint;
import org.byteinfo.socket.Node;
import org.byteinfo.util.function.Unchecked;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SequencedMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataService {
	private static final Map<String, ContactModel> contacts = new ConcurrentHashMap<>(); // address -> contact
	private static final Map<String, SequencedMap<String, MessageModel>> chats = new ConcurrentHashMap<>(); // address -> msg id -> msg model
	private static final Map<String, Node> outgoings = new ConcurrentHashMap<>(); // address -> node
	private static final Map<String, File> sentFiles = new ConcurrentHashMap<>(); // msgId -> file
	private static final Map<String, File> receivedFiles = new ConcurrentHashMap<>(); // msgId -> file

	private static volatile ContactController contactController;
	private static volatile ChatController chatController;
	private static volatile ContentController contentController;
	private static volatile DatagramSocket datagramSocket;

	public static void init(Context context) {
		contactController = context.instance(ContactController.class);
		chatController = context.instance(ChatController.class);
		contentController = context.instance(ContentController.class);

		Thread.startVirtualThread(() -> {
			try (var endpoint = new Endpoint(new InetSocketAddress(Config.PORT))) {
				while (true) {
					var accepted = endpoint.accept();
					Thread.startVirtualThread(Unchecked.runnable(() -> {
						try (accepted) {
							while (true) {
								var message = accepted.readMessage();
								if (message == null) {
									break;
								}
								var address = accepted.address().getAddress().getHostAddress();
								switch (message.type()) {
									case Constant.MESSAGE_TEXT -> {
										var msg = JSON.parse(new String(message.bytes()), TextMessage.class);
										addMessage(address, new TextMessageModel(msg.id(), msg.time(), Constant.STATUS_COMPLETED, true, msg.content()));
									}
									case Constant.MESSAGE_DATA -> {
										var msg = JSON.parse(new String(message.bytes()), DataMessage.class);
										addMessage(address, new DataMessageModel(msg.id(), msg.time(), Constant.STATUS_PENDING, true, msg.name(), msg.size(), msg.modified(), 0));
									}
									case Constant.MESSAGE_DATA_REQUEST -> {
										var bytes = message.bytes();
										var msgId = new String(bytes);
										var file = sentFiles.get(msgId);
										if (file == null) {
											send(address, Constant.MESSAGE_DATA_EXPIRED, bytes);
										} else {
											try (var node = new Node(new InetSocketAddress(accepted.address().getAddress(), Config.PORT)).connect()) {
												var length = file.length();
												node.writeMessage(Constant.MESSAGE_DATA_RESPONSE, length + bytes.length, new ByteArrayInputStream(bytes));
												try (var in = new BufferedInputStream(Files.newInputStream(file.toPath()), Config.BUFFER_SIZE)) {
													long sent = 0;
													while (sent < length) {
														var chunk = in.readNBytes(Config.CHUNK_SIZE);
														node.write(chunk);
														sent += chunk.length;
														updateDataProgress(address, msgId, Constant.STATUS_TRANSFERRING, (int) (sent * 100 / length));
													}
												}
												updateDataProgress(address, msgId, Constant.STATUS_COMPLETED, 0);
											}
										}
									}
									case Constant.MESSAGE_DATA_RESPONSE -> {
										try (var in = message.stream()) {
											var msgId = new String(in.readNBytes(Constant.UUID_LENGTH));
											var file = receivedFiles.get(msgId);
											if (file == null) {
												return;
											}
											try (var out = new BufferedOutputStream(Files.newOutputStream(file.toPath()), Config.BUFFER_SIZE)) {
												var length = message.length() - Constant.UUID_LENGTH;
												var received = 0L;
												while (received < length) {
													var bytes = in.readNBytes(Config.CHUNK_SIZE);
													out.write(bytes);
													received += bytes.length;
													updateDataProgress(address, msgId, Constant.STATUS_TRANSFERRING, (int) (received * 100 / length));
												}
											}
											var path = file.toPath();
											var name = file.getName();
											Files.move(path, path.resolveSibling(name.substring(0, name.length() - Config.TMP_SUFFIX.length())));
											receivedFiles.remove(msgId);
											updateDataProgress(address, msgId, Constant.STATUS_COMPLETED, 0);
										}
									}
									case Constant.MESSAGE_DATA_EXPIRED -> {
										updateDataProgress(address, new String(message.bytes()), Constant.STATUS_EXPIRED, 0);
									}
									case Constant.MESSAGE_ACTIVATE -> Platform.runLater(() -> {
										App.stage.setIconified(false);
										App.stage.show();
										App.stage.toFront();
									});
								}
							}
						}
					}));
				}
			} catch (BindException e) {
				try (var node = new Node(new InetSocketAddress(InetAddress.getLocalHost(), Config.PORT))) {
					node.writeMessage(Constant.MESSAGE_ACTIVATE, new byte[0]);
				} catch (Exception ex) {
					// ignore
				}
			} catch (Exception e) {
				Log.error(e);
			} finally {
				Platform.exit();
			}
		});

		Thread.startVirtualThread(Unchecked.runnable(() -> {
			datagramSocket = new DatagramSocket(Config.PORT);
			DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
			while (true) {
				try {
					datagramSocket.receive(packet);
					var msg = JSON.parse(new String(packet.getData()), ControlMessage.class);
					if (Device.ID.equals(msg.id())) {
						continue;
					}
					var address = packet.getAddress().getHostAddress();
					switch (msg.type()) {
						case Constant.BROADCAST_DISCOVER -> {
							addContact(address, msg);
							var data = JSON.stringify(new ControlMessage(Constant.BROADCAST_RESPONSE, Device.ID, Device.NAME, Device.TYPE)).getBytes();
							var rsp = new DatagramPacket(data, data.length, packet.getAddress(), Config.PORT);
							datagramSocket.send(rsp);
						}
						case Constant.BROADCAST_RESPONSE -> addContact(address, msg);
						case Constant.BROADCAST_QUIT -> removeContact(address);
					}
				} catch (Exception e) {
					Log.error(e);
				}
			}
		}));

		refreshContacts();
	}

	public static boolean sendTextMessage(String address, String content) {
		var msg = new TextMessage(UUID.randomUUID().toString(), System.currentTimeMillis(), content);
		var succeeded = send(address, Constant.MESSAGE_TEXT, JSON.stringify(msg).getBytes());
		if (succeeded) {
			addMessage(address, new TextMessageModel(msg.id(), msg.time(), Constant.STATUS_COMPLETED, false, content));
		}
		return succeeded;
	}

	public static boolean sendDataMessage(String address, File file) {
		var msg = new DataMessage(UUID.randomUUID().toString(), System.currentTimeMillis(), file.getName(), file.length(), file.lastModified());
		var succeeded = send(address, Constant.MESSAGE_DATA, JSON.stringify(msg).getBytes());
		if (succeeded) {
			sentFiles.put(msg.id(), file);
			addMessage(address, new DataMessageModel(msg.id(), msg.time(), Constant.STATUS_PENDING, false, msg.name(), msg.size(), msg.modified(), 0));
		}
		return succeeded;
	}

	public static boolean sendDataRequest(String address, String msgId, File target) {
		var succeeded = send(address, Constant.MESSAGE_DATA_REQUEST, msgId.getBytes());
		if (succeeded) {
			receivedFiles.put(msgId, target);
		}
		return succeeded;
	}

	public static void refreshContacts() {
		contacts.clear();
		for (Node node : outgoings.values()) {
			Thread.startVirtualThread(Unchecked.runnable(node::disconnect));
		}
		outgoings.clear();
		broadcast(Constant.BROADCAST_DISCOVER);
	}

	public static void quit() {
		broadcast(Constant.BROADCAST_QUIT);
	}

	public static SequencedMap<String, MessageModel> getChat(String address) {
		return chats.getOrDefault(address, Collections.emptySortedMap());
	}

	private static void addContact(String address, ControlMessage message) {
		if (contacts.containsKey(address)) {
			return;
		}
		var contact = new ContactModel(address, message.name(), message.device());
		contacts.put(address, contact);
		var node = new Node(new InetSocketAddress(address, Config.PORT));
		outgoings.put(address, node);
		Thread.startVirtualThread(() -> {
			try {
				node.connect();
				Platform.runLater(() -> contactController.addContact(contact));
				while (true) {
					node.readExact(1);
				}
			} catch (Exception e) {
				// ignore
			} finally {
				removeContact(address);
			}
		});
	}

	private static void removeContact(String address) {
		contacts.remove(address);
		Platform.runLater(() -> contactController.removeContact(address));
		var node = outgoings.remove(address);
		if (node != null) {
			Thread.startVirtualThread(Unchecked.runnable(node::disconnect));
		}
	}

	private static void updateDataProgress(String address, String msgId, int status, int progress) {
		var chat = chats.get(address);
		if (chat == null) {
			return;
		}
		synchronized (chat) {
			var message = chat.get(msgId);
			if (message instanceof DataMessageModel msg) {
				if (msg.getStatus() != status || msg.getProgress() != progress) {
					msg.setStatus(status);
					msg.setProgress(progress);
					Platform.runLater(() -> contentController.updateProgress(msg));
				}
			}
		}
	}

	private static boolean send(String address, int type, byte[] data) {
		var node = outgoings.get(address);
		if (node == null) {
			return false;
		}
		Unchecked.runnable(() -> node.writeMessage(type, data)).run();
		return true;
	}

	private static void broadcast(int broadcastType) {
		Thread.startVirtualThread(Unchecked.runnable(() -> {
			var data = JSON.stringify(new ControlMessage(broadcastType, Device.ID, Device.NAME, Device.TYPE)).getBytes();
			for (InetAddress address : NetUtil.getBroadcastAddresses()) {
				var packet = new DatagramPacket(data, data.length, address, Config.PORT);
				datagramSocket.send(packet);
			}
		}));
	}

	private static void addMessage(String address, MessageModel messageModel) {
		var contact = contacts.get(address);
		if (contact == null) {
			return;
		}
		var chat = chats.computeIfAbsent(contact.address(), key -> new LinkedHashMap<>());
		synchronized (chat) {
			chat.putLast(messageModel.getId(), messageModel);
		}
		Platform.runLater(() -> {
			chatController.addMessage(contact, messageModel);
			contentController.addMessage(contact, messageModel);
		});
	}
}

