package com.wx.openfire.ch1;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.sm.predicates.ForEveryStanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import java.io.IOException;

/**
 * @author xinquan.huangxq
 */
public class Client {

    private String userName;

    private String userPwd;

    private String resource;

    private String serviceName;

    private String host;

    private int port;

    private AbstractXMPPConnection connection;

    public Client(String host, int port, String serviceName, String resource, String userName, String userPwd) {
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
        this.resource = resource;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    /**
     * 发送一条信息
     *
     * @param userName
     * @param content
     * @return
     */
    public boolean sendMsg(String userName, String content) {
        if (this.connection == null) {
            return false;
        }
        Message message = new Message(userName + "@" + serviceName, content);
        String deliveryReceiptId = DeliveryReceiptRequest.addTo(message);
        try {
            connection.sendStanza(message);
            System.out.println("sendMessage: deliveryReceiptId for this message is: "+ deliveryReceiptId);
            return true;
        } catch (SmackException.NotConnectedException e) {
            return false;
        }
    }

    /**
     * 初始化
     */
    public void init() throws IOException, XMPPException, SmackException {
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(userName, userPwd);
        configBuilder.setResource(resource);
        configBuilder.setServiceName(serviceName);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setHost(host);
        configBuilder.setPort(port);
        configBuilder.setSendPresence(true);
        XMPPTCPConnectionConfiguration config = configBuilder.build();

        AbstractXMPPConnection connection = new XMPPTCPConnection(config);
        connection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                System.out.println("Openfire: connected");
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                System.out.println("Openfire: authenticated");
            }

            @Override
            public void connectionClosed() {
                System.out.println("Openfire: connectionClosed");
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                System.out.println("Openfire: connectionClosedOnError");
            }

            @Override
            public void reconnectionSuccessful() {
                System.out.println("Openfire: reconnectionSuccessful");
            }

            @Override
            public void reconnectingIn(int seconds) {
                System.out.println("Openfire: reconnectingIn");
            }

            @Override
            public void reconnectionFailed(Exception e) {
                System.out.println("Openfire: reconnectionFailed");
            }
        });
        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        reconnectionManager.enableAutomaticReconnection();

        connection.connect();
        connection.login();

        connection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {
                System.out.println("Received Openfire message: " + stanza);
            }
        }, ForEveryStanza.INSTANCE);

        PingManager.getInstanceFor(connection).setPingInterval(30);

        // 记录connection
        this.connection = connection;
    }
}
