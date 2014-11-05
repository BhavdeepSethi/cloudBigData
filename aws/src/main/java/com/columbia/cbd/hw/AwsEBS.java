package com.columbia.cbd.hw;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AwsEBS {

    static AmazonEC2 ec2;
    static int sleepCount=0;

    public static void main(String[] args) throws IOException {

        String userName = "ec2-user";


        AWSCredentials credentials = new PropertiesCredentials(
                AwsEBS.class.getClassLoader().getResourceAsStream("AwsCredentials.properties"));

        System.out.println("#1 Create Amazon Client object");
        ec2 = new AmazonEC2Client(credentials);

        try {
            System.out.println("#2 Describe Current Instances");
            DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
            List<Reservation> reservations = describeInstancesRequest.getReservations();
            Set<Instance> instances = new HashSet<Instance>();
            // add all instances to a Set.
            for (Reservation reservation : reservations) {
                instances.addAll(reservation.getInstances());
            }

            String instanceId = null;
            String availabilityZone = null;
            String hostName = null;
            System.out.println("You have " + instances.size() + " Amazon EC2 instance(s).");
            for (Instance ins : instances){

                // instance state
                InstanceState is = ins.getState();


                if(is.getName().equals("running")) {
                    // instance id
                    hostName = ins.getPublicDnsName();
                    instanceId = ins.getInstanceId();
                    availabilityZone = ins.getPlacement().getAvailabilityZone();
                }

                List<InstanceBlockDeviceMapping> blockDeviceMappings = ins.getBlockDeviceMappings();
                for(InstanceBlockDeviceMapping ibdMapping : blockDeviceMappings){
                    System.out.println("EBS id: " + ibdMapping.getEbs().getVolumeId());
                    System.out.println("EBS Name: "+ibdMapping.getDeviceName());
                }
                System.out.println(ins.getInstanceId()+" "+is.getName() +" in "+ ins.getPlacement().getAvailabilityZone());
            }
            if(null == hostName){
                System.out.println("No active instance running. Run mini hw2 to create an instance.");
                System.exit(0);
            }

            CreateVolumeRequest createVolumeRequest = new CreateVolumeRequest()
                    .withAvailabilityZone(availabilityZone) // The AZ in which to create the volume.
                    .withSize(1).withVolumeType(VolumeType.Standard); // The size of the volume, in gigabytes.

            CreateVolumeResult createVolumeResult = ec2.createVolume(createVolumeRequest);
            String volumeId = createVolumeResult.getVolume().getVolumeId();
            System.out.println("Volume: "+volumeId);
            System.out.println("Attachments: ");

            for(VolumeAttachment attachment : createVolumeResult.getVolume().getAttachments()){
                System.out.println(attachment.getDevice()+" is attached to "+attachment.getInstanceId());
            }

            DescribeVolumesRequest describeVolumesRequest = new DescribeVolumesRequest();
            describeVolumesRequest.withVolumeIds(volumeId);

            String volState = getVolumeState(describeVolumesRequest);
            while(null == volState){
                sleepCount+=10000;
                System.out.println("Volume not ready. Sleeping for "+(sleepCount/1000)+" seconds. ");
                try {
                    Thread.sleep(sleepCount);
                } catch (InterruptedException ie) {
                    //Handle exception
                }
                volState = getVolumeState(describeVolumesRequest);
            }
            System.out.println("Volume: "+volumeId+" is "+volState);

            Random r = new Random();
            //Generating random character from b to z
            char c = (char)(r.nextInt(25) + 'b');
            String deviceId = "/dev/sd"+c;
            AttachVolumeRequest attachRequest = new AttachVolumeRequest()
                    .withInstanceId(instanceId).withVolumeId(volumeId).withDevice(deviceId);

            AttachVolumeResult attachResult = ec2.attachVolume(attachRequest);

            System.out.println(attachResult.getAttachment().getDevice()+" attached to "+ attachResult.getAttachment().getInstanceId());

            System.out.println("Waiting 30 sec for ec2 instance to attach the image");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
            String path = AwsEBS.class.getClassLoader().getResource("ConsolePair2.pem").getPath();


            AwsSShClient client = new AwsSShClient(hostName, userName, path, "");
            client.connectToRemote();
            String folder = "/ebs"+r.nextInt();
            System.out.println(client.runCommand("lsblk"));
            System.out.println("-------------------------------------");
            System.out.println("Creating an ext3 file system on the volume");
            System.out.println(client.runCommand("sudo mkfs -t ext3 "+deviceId));
            System.out.println("-------------------------------------");
            System.out.println("Creating directory: "+folder);
            System.out.println(client.runCommand("sudo mkdir "+folder));
            System.out.println("-------------------------------------");
            System.out.println("Mounting "+deviceId+" to "+folder);
            System.out.println(client.runCommand("sudo mount "+deviceId+" "+folder));
            System.out.println(client.runCommand("lsblk"));
            System.out.println("-------------------------------------");


        }catch (AmazonServiceException ase){
            System.out.println("Caught Exception: " + ase.getMessage());
            System.out.println("Reponse Status Code: " + ase.getStatusCode());
            System.out.println("Error Code: " + ase.getErrorCode());
            System.out.println("Request ID: " + ase.getRequestId());
        }catch (URISyntaxException e){
            e.printStackTrace();
        }

    }

    public static String getVolumeState(DescribeVolumesRequest describeVolumesRequest) {
        DescribeVolumesResult describeVolumesResult = ec2.describeVolumes(describeVolumesRequest);

        List<Volume> volumeList = describeVolumesResult.getVolumes();

        for (Volume volume : volumeList) {
            if(volume.getState().equals("available"))
                return volume.getState();
        }
        return null;
    }


}
