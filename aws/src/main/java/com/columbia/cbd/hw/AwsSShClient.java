package com.columbia.cbd.hw;


import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class AwsSShClient {

    private SSHClient ssh;
    private String keyVerifier;
    private String host;
    private String username;
    private String keyLoc;

    AwsSShClient(String hostName, String username, String keyLoc, String keyVerifier){
        ssh = new SSHClient();
        this.host = hostName;
        this.username = username;
        this.keyVerifier = keyVerifier;
        this.keyLoc = keyLoc;
    }


    AwsSShClient(String hostName, String username){
        this(hostName, username, null, null);
    }

    public void connectToRemote() throws IOException, URISyntaxException {

        /*******************************************
        Ideally the public key should be in a common location like /etc/aws/public-key or ~/.ssh/
        Adding it to resources so that code can be run from anywhere just for sample purposes.
        ********************************************/

        KeyProvider kp = ssh.loadKeys(getKeyLoc());
        if(null != getKeyVerifier())
            ssh.addHostKeyVerifier(getKeyVerifier());
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.connect(getHost());
        System.out.println("Is connected? : " + ssh.isConnected());
        ssh.authPublickey(getUsername(), kp);

        System.out.println("Is Authenticated? : " + ssh.isAuthenticated());

    }

    public boolean isConnected() {
        return ssh.isConnected();
    }

    public void disconnect() throws IOException {
        if(ssh.isConnected())
            ssh.disconnect();
    }

    final String runCommand(String execCommand) throws IOException {
        final Session session = ssh.startSession();
        session.allocateDefaultPTY();
        try {
            final Session.Command command = session.exec(execCommand);
            String response = IOUtils.readFully(command.getInputStream()).toString();
            command.join(10, TimeUnit.SECONDS);
            return response;
        } finally {
            session.close();
        }

    }

    public void scpFiles(String src) throws IOException {
        ssh.newSCPFileTransfer().upload(new FileSystemFile(src), "");
    }

    public String getKeyVerifier() {
        return keyVerifier;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getKeyLoc() {
        return keyLoc;
    }

}
