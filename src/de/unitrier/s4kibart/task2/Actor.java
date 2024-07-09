package de.unitrier.s4kibart.task2;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Network;
import org.oxoo2a.sim4da.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Random;

public class Actor extends Node {
    private boolean active = true;
    private String [] otherParticipants;
    Random rand = new Random();
    private float p = 1.f;
    private final String name;
    private Logger logger = LoggerFactory.getLogger(Network.class);

    private void shuffleParticipants(){
        for (int i = otherParticipants.length-1; i > 0; i--){
            int index = rand.nextInt(i+1);
            String tmp = otherParticipants[index];
            otherParticipants[index] = otherParticipants[i];
            otherParticipants[i] = tmp;
        }
    }

    private void sendMessageToRandomSubset(Message m){
        shuffleParticipants();
        int toIndex = rand.nextInt(otherParticipants.length);
        for (int i = 0; i < toIndex; i++) {
            sendBlindly(m, otherParticipants[i]);
        }
    }

    public Actor(String name) {
        super(name);
        this.name = name;
        rand.setSeed(name.hashCode());
    }
    public String getName(){
        return name;
    }
    public void setOtherParticipants(String[] otherParticipants) {
        this.otherParticipants = otherParticipants;
    }
    @Override
    protected void engage(){
        Message m = new Message().add("Firework", 1);
        m.addHeader("type", "normal_message");
        int waitTime = (int) (4000*rand.nextFloat());
        try {
            Thread.sleep(waitTime);
            sendMessageToRandomSubset(m);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        active = false;
        while (true){
            m = receive();
            if (m.queryInteger("Firework") == 1){
                active = true;
            }
            if (active){
                if (rand.nextFloat() < p){
                    sendMessageToRandomSubset(m);
                }
                p = p / 2;
                active = false;
            }
        }
    }
}