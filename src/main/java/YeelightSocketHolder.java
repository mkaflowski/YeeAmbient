//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.mollin.yapi.command.YeelightCommand;
import com.mollin.yapi.enumeration.YeelightAdjustAction;
import com.mollin.yapi.enumeration.YeelightAdjustProperty;
import com.mollin.yapi.enumeration.YeelightEffect;
import com.mollin.yapi.enumeration.YeelightProperty;
import com.mollin.yapi.exception.YeelightResultErrorException;
import com.mollin.yapi.exception.YeelightSocketException;
import com.mollin.yapi.flow.YeelightFlow;
import com.mollin.yapi.result.YeelightResultError;
import com.mollin.yapi.result.YeelightResultOk;
import com.mollin.yapi.utils.YeelightUtils;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class YeelightSocketHolder {
    private static int SOCKET_TIMEOUT = 1000;
    private String ip;
    private int port;
    private Socket socket;
    private BufferedReader socketReader;
    private BufferedWriter socketWriter;

    public YeelightSocketHolder(String ip, int port) throws YeelightSocketException {
        this.ip = ip;
        this.port = port;
        this.initSocketAndStreams();
    }

    private void initSocketAndStreams() throws YeelightSocketException {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(this.ip, this.port);
            this.socket = new Socket();
            this.socket.connect(inetSocketAddress, SOCKET_TIMEOUT);
            this.socket.setSoTimeout(SOCKET_TIMEOUT);
            this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.socketWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (Exception var2) {
            throw new YeelightSocketException(var2);
        }
    }

    public void send(String datas) throws YeelightSocketException {
        try {
            this.socketWriter.write(datas);
            this.socketWriter.flush();
        } catch (Exception e) {
            throw new YeelightSocketException(e);
        }
    }

    public String readLine() throws YeelightSocketException {
        try {
            String datas = this.socketReader.readLine();
            Logger.debug("{} received from {}:{}", new Object[]{datas, this.ip, this.port});
            return datas;
        } catch (Exception var2) {
            throw new YeelightSocketException(var2);
        }
    }
}
