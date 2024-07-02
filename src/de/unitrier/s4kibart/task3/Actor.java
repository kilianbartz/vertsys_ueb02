package de.unitrier.s4kibart.task3;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;

import java.util.Random;

public class Actor extends Node {
    private boolean active = true;
    private String [] otherParticipants;
    Random rand = new Random();
    private float p = 1.f;
    private final String name;
    private int noSent = 0;
    private int noReceived = 0;

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
        System.out.println("Node " + name + "sends Message to " + toIndex + " other nodes.");
        for (int i = 0; i < toIndex; i++) {
            sendBlindly(m, otherParticipants[i]);
        }
        noSent += toIndex;
    }

    public Actor(String name) {
        super(name);
        this.name = name;
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
        int waitTime = (int) (Math.random()*4000);
        try {
            Thread.sleep(waitTime);
            sendMessageToRandomSubset(m);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        active = false;
        while (true){
            Message rec = receive();
            if (rec.queryHeader("type").equals("normal_message")){
                noReceived++;
                if (rec.queryInteger("Firework") == 1){
                    active = true;
                }
                if (active){
                    float sendMsg = rand.nextFloat();
                    if (sendMsg < p){
                        sendMessageToRandomSubset(m);
                    }
                    p = p / 2;
                    active = false;
                }
            } else if (rec.queryHeader("type").equals("observer_message")) {
                Message answer = new Message();
                if (rec.query("command").equals("query")){
                    answer.add("noSent", noSent);
                    answer.add("noReceived", noReceived);
                    sendBlindly(answer, rec.queryHeader("sender"));
                } else if (rec.query("command").equals("stop")){
                    break;
                    // Observer hat Terminierung festgestellt --> stoppe
                }
            }
        }
    }
}