package com.wx.openfire.ch1;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * @author xinquan.huangxq
 */
public class Main {

    public static void main(String[] args) throws XMPPException, IOException, SmackException {
        Client client = new Client("192.168.171.130", 5222, "ubuntu", "openfire-ch1", "admin", "WMI?wangxuan");
        client.init();

        client.sendMsg("jp", "you are a boy");
        client.sendMsg("jp", "你是个好人");
    }
}
