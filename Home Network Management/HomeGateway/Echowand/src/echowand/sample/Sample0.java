package echowand.sample;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.*;

public class Sample0 {

    public static final String peerAddress = "192.168.0.109";

    public static CommonFrame createCommonFrameGet() {
        CommonFrame commonFrame = new CommonFrame(new EOJ("013001"), new EOJ("0ef001"), ESV.Get);
        commonFrame.setTID((short)1);
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
        payload.addFirstProperty(new Property(EPC.x80));
        payload.addFirstProperty(new Property(EPC.x88));
        payload.addFirstProperty(new Property(EPC.x9F));
        payload.addFirstProperty(new Property(EPC.x9E));
        payload.addFirstProperty(new Property(EPC.x9D));
        payload.addFirstProperty(new Property(EPC.xD5));
        payload.addFirstProperty(new Property(EPC.xD6));
        payload.addFirstProperty(new Property(EPC.xD7));
        return commonFrame;
    }

    public static CommonFrame createCommonFrameSetGet() {
        CommonFrame commonFrame = new CommonFrame(new EOJ("013001"), new EOJ("013001"), ESV.SetGet);
        commonFrame.setTID((short)2);
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
        payload.addFirstProperty(new Property(EPC.xB0, new Data((byte)0x12)));
        payload.addSecondProperty(new Property(EPC.x80));
        payload.addSecondProperty(new Property(EPC.x9F));
        payload.addSecondProperty(new Property(EPC.x9E));
        payload.addSecondProperty(new Property(EPC.x9D));
        return commonFrame;
    }

    public static CommonFrame createCommonFrameINFC() {
        CommonFrame commonFrame = new CommonFrame(new EOJ("013001"), new EOJ("0ef001"), ESV.INFC);
        commonFrame.setTID((short)3);
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x31)));
        return commonFrame;
    }

    public static void setTimeout(final int timeout) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.exit(1);
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }
    
    public static void main(String[] args) {

        setTimeout(3000);

        try {

            Inet4Subnet subnet = Inet4Subnet.startSubnet();

            //========================= Get =========================
            Node remoteNode1 = subnet.getRemoteNode((Inet4Address) Inet4Address.getByName(peerAddress));

            Frame frame1 = new Frame(subnet.getLocalNode(), remoteNode1, createCommonFrameGet());
            
            System.out.println("Sending:  " + frame1);
            subnet.send(frame1);

            System.out.println("Received: " + subnet.receive());
            System.out.println();
            
            
            //========================= SetGet =========================
            Node remoteNode2 = subnet.getRemoteNode((Inet4Address)Inet4Address.getByName(peerAddress));
            
            Frame frame2 = new Frame(subnet.getLocalNode(), remoteNode2, createCommonFrameSetGet());
            
            System.out.println("Sending:  " + frame2);
            subnet.send(frame2);
            
            System.out.println("Received: " + subnet.receive());
            System.out.println();
            
            
            //========================= INFC =========================
            Frame frame3 = new Frame(subnet.getLocalNode(), subnet.getGroupNode(), createCommonFrameINFC());
            
            System.out.println("Sending:  " + frame3);
            subnet.send(frame3);
            
            for (;;) {
                System.out.println("Received: " + subnet.receive());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SubnetException e) {
            e.printStackTrace();
        }
    }
}
