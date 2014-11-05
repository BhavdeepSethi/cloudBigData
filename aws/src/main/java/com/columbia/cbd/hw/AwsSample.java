package com.columbia.cbd.hw;/*
 * Copyright 2010 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * Modified by Sambit Sahu
 * Modified by Kyung-Hwa Kim (kk2515@columbia.edu)
 * 
 * 
 */
import java.io.*;
import java.net.ConnectException;
import java.util.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;


public class AwsSample {

    /*
     * Important: Be sure to fill in your AWS access credentials in the
     *            AwsCredentials.properties file before you try to run this
     *            sample.
     * http://aws.amazon.com/security-credentials
     */

    static AmazonEC2      ec2;
    static int sleepCount=0;

    public static void main(String[] args) throws Exception {

        AWSCredentials credentials = new PropertiesCredentials(
                AwsSample.class.getClassLoader().getResourceAsStream("AwsCredentials.properties"));

        /*********************************************
         *
         *  #1 Create Amazon Client object
         *
         *********************************************/
        System.out.println("#1 Create Amazon Client object");
        ec2 = new AmazonEC2Client(credentials);


        String securityGroupName = "JavaSecurityGroup";
        try {
            /*********************************************
             *
             *  #1.1 Create Security Group
             *
             *********************************************/

            System.out.println("#1.1 Create Security Group");
            CreateSecurityGroupRequest createSecurityGroupRequest =
                    new CreateSecurityGroupRequest();

            createSecurityGroupRequest.withGroupName(securityGroupName)
                    .withDescription("CBD Security Group");

            CreateSecurityGroupResult createSecurityGroupResult =
                    ec2.createSecurityGroup(createSecurityGroupRequest);

            System.out.println("VPC Id: " + createSecurityGroupResult.getGroupId());

            /*********************************************
             *
             *  #1.2 Give Security Permissions: HTTP, SSH, TCP
             *
             *********************************************/

            Collection<IpPermission> ips = new ArrayList<IpPermission>();

            //SSH Permissions
            IpPermission sshPermission = new IpPermission();
            sshPermission.withIpRanges("0.0.0.0/0").withIpProtocol("TCP").withFromPort(22).withToPort(22);

            //HTTP Permissions
            IpPermission httpPermission = new IpPermission();
            httpPermission.withIpRanges("0.0.0.0/0").withIpProtocol("TCP").withFromPort(80).withToPort(80);

            //TCP Permissions
            IpPermission tcpPermission = new IpPermission();
            tcpPermission.withIpRanges("0.0.0.0/0").withIpProtocol("TCP").withFromPort(0).withToPort(65535);

            ips.add(sshPermission);
            ips.add(httpPermission);
            ips.add(tcpPermission);

            AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =
                    new AuthorizeSecurityGroupIngressRequest();

            authorizeSecurityGroupIngressRequest.withGroupName("JavaSecurityGroup")
                    .withIpPermissions(ips);

            ec2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);


        }catch (AmazonServiceException ase) {
            System.out.println("Security Group Already exists. Continuing.");
        }
        try{

            /*********************************************
             *
             *  #1.3 Key Pair
             *
             *********************************************/

            System.out.println("#1.2 Key Pair ");
            Random random = new Random();
            int keyNo = random.nextInt();
            String keyName = "cbd-keypair"+keyNo;

            CreateKeyPairRequest createKeyPairRequest =
                    new CreateKeyPairRequest();

            createKeyPairRequest.withKeyName(keyName);

            CreateKeyPairResult createKeyPairResult =
                    ec2.createKeyPair(createKeyPairRequest);
            System.out.println("Key Pair Generated with name: "+keyName);

            KeyPair keyPair = createKeyPairResult.getKeyPair();
            String privateKey = keyPair.getKeyMaterial();

            String fingerPrint = keyPair.getKeyFingerprint();
            try {
                File file = new File(keyName+".pem");
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                output.write(privateKey);
                output.close();
                file.setReadable(false);
                file.setReadable(true, true);
                file.setWritable(false);
                file.setExecutable(false);
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            System.out.println("Key Pair downloaded!");

            /*********************************************
             *
             *  #2 Describe Availability Zones.
             *
             *********************************************/
            System.out.println("#2 Describe Availability Zones.");
            DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
            System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
                    " Availability Zones.");

            /*********************************************
             *
             *  #3 Describe Available Images
             *
             *********************************************/
            System.out.println("#3 Describe Available Images");
            DescribeImagesResult dir = ec2.describeImages();
            List<Image> images = dir.getImages();
            System.out.println("You have " + images.size() + " Amazon images");


            /*********************************************
             *
             *  #4 Describe Key Pair
             *
             *********************************************/
            System.out.println("#9 Describe Key Pair");
            DescribeKeyPairsResult dkr = ec2.describeKeyPairs();
            System.out.println(dkr.toString());

            /*********************************************
             *
             *  #5 Describe Current Instances
             *
             *********************************************/
            System.out.println("#4 Describe Current Instances");
            DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
            List<Reservation> reservations = describeInstancesRequest.getReservations();
            Set<Instance> instances = new HashSet<Instance>();
            // add all instances to a Set.
            for (Reservation reservation : reservations) {
                instances.addAll(reservation.getInstances());
            }

            System.out.println("You have " + instances.size() + " Amazon EC2 instance(s).");
            for (Instance ins : instances){

                // instance id
                String instanceId = ins.getInstanceId();

                // instance state
                InstanceState is = ins.getState();
                System.out.println(instanceId+" "+is.getName());
            }

            /*********************************************
             *
             *  #6 Create an Instance
             *
             *********************************************/


            System.out.println("#5 Create an Instance");
            String imageId = "ami-76f0061f"; //Basic 32-bit Amazon Linux AMI
            int minInstanceCount = 1; // create 1 instance
            int maxInstanceCount = 1;



            RunInstancesRequest rir = new RunInstancesRequest(imageId, minInstanceCount, maxInstanceCount);
            rir.withInstanceType("m1.small").withKeyName(keyName).withSecurityGroups(securityGroupName);
            RunInstancesResult result = ec2.runInstances(rir);

            //get instanceId from the result
            List<Instance> resultInstance = result.getReservation().getInstances();

            String createdInstanceId = null;

            for (Instance ins : resultInstance){
                createdInstanceId = ins.getInstanceId();
                System.out.println("New instance has been created: "+createdInstanceId);
            }


            /*********************************************
             *
             *  #7 Create a 'tag' for the new instance.
             *
             *********************************************/
            System.out.println("#6 Create a 'tag' for the new instance.");
            List<String> resources = new LinkedList<String>();
            List<Tag> tags = new LinkedList<Tag>();
            Tag nameTag = new Tag("Name", "MyFirstInstance");

            resources.add(createdInstanceId);
            tags.add(nameTag);

            CreateTagsRequest ctr = new CreateTagsRequest(resources, tags);
            ec2.createTags(ctr);


            /*********************************************
             *
             *  #8 Stop/Start an Instance
             *
             *********************************************/


            System.out.println("#7 Stop the Instance");
            List<String> instanceIds = new LinkedList<String>();
            instanceIds.add(createdInstanceId);

            //stop
            StopInstancesRequest stopIR = new StopInstancesRequest(instanceIds);
            //ec2.stopInstances(stopIR);


            //start
            System.out.println("#8 Starting the Instance");
            StartInstancesRequest startIR = new StartInstancesRequest(instanceIds);
            //ec2.startInstances(startIR);

            /**************************************
            *  Getting Public IP of the created instance.
            ***************************************/

            String createdIP = getInstancePublicIpAddress(createdInstanceId);
            while(null == createdIP){
                sleepCount+=10000;
                System.out.println("DNS not assigned. Sleeping for "+(sleepCount/1000)+" seconds. ");
                try {
                    Thread.sleep(sleepCount);
                } catch (InterruptedException ie) {
                    //Handle exception
                }
                createdIP = getInstancePublicIpAddress(createdInstanceId);
            }
            System.out.println("# 8.1 Public IP assigned: "+createdIP+" to new instance: "+createdInstanceId);


            /***************************************
            *  SSH into the created instance programmatically
            ****************************************/

            String username = "ec2-user"; //default username
            AwsSShClient awsSShClient = new AwsSShClient(createdIP, username, keyName+".pem", fingerPrint);
            int sleep = 60000;
            while(!awsSShClient.isConnected()){
                System.out.println("Sleeping "+(sleep/1000)+" seconds to let the ec2 instance open connections");
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException ie) {
                    System.out.println("Interrupted!");
                }
                try{
                    System.out.println("#8.2 Trying to SSH into "+createdIP);
                    awsSShClient.connectToRemote();
                    System.out.println("Connected to "+createdIP);
                }catch (ConnectException c){
                    System.out.println("Socket Time out. Retrying.");
                }

            }
            System.out.println("Running remote command (whoami) on "+createdIP);
            String response = awsSShClient.runCommand("whoami");
            System.out.println("Response: "+response);
            awsSShClient.disconnect();

            /*********************************************
             *
             *  #9 Terminate an Instance
             *
             *********************************************/
            //     System.out.println("#8 Terminate the Instance");
            //     TerminateInstancesRequest tir = new TerminateInstancesRequest(instanceIds);
            //     ec2.terminateInstances(tir);


            /*********************************************
             *
             *  #10 shutdown client object
             *
             *********************************************/
            ec2.shutdown();

        } catch (AmazonServiceException ase) {
            System.out.println("Caught Exception: " + ase.getMessage());
            System.out.println("Reponse Status Code: " + ase.getStatusCode());
            System.out.println("Error Code: " + ase.getErrorCode());
            System.out.println("Request ID: " + ase.getRequestId());
        }

    }


    public static String getInstancePublicIpAddress(String instanceId) {
        DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
        List<Reservation> reservations = describeInstancesRequest.getReservations();
        for (Reservation reservation : reservations) {
            for (Instance instance : reservation.getInstances()) {
                if (instance.getInstanceId().equals(instanceId) && instance.getPublicIpAddress()!=null)
                    return instance.getPublicIpAddress();
            }
        }
        return null;
    }


}
