package de.unitrier.s4kibart.task3;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;

public class Observer extends Node {

    private Message m;
    private String[] participants;
    private boolean terminated = false;

    private Pair couldBeTerminated(){
        System.out.println("Starting Observer query...");
        for (String participant : participants) {
            sendBlindly(m, participant);
        }
        int noMessagesSent = 0;
        int noMessagesReceived = 0;
        for (int i = 0; i < participants.length; i++) {
            Message answer = receive();
            noMessagesSent += answer.queryInteger("noSent");
            noMessagesReceived += answer.queryInteger("noReceived");
        }
        System.out.println("Result of Observer Queries: noSent" + noMessagesSent);
        System.out.println("Result of Observer Queries: noReceived" + noMessagesReceived);
        return new Pair(noMessagesSent, noMessagesReceived);
    }

    public Observer(String name, String[] participants) {
        super(name);
        m = new Message();
        m.addHeader("type", "observer_message");
        m.add("command", "query");
        this.participants = participants;
    }

    @Override
    protected void engage(){
        Pair lastWave = null;
        while(!terminated){
            Pair firstWave = couldBeTerminated();
            if (!firstWave.corresponds()) {
                try {
                    Thread.sleep(1000);
                    continue;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (lastWave != null && firstWave.getFirst() == lastWave.getSecond())
                terminated = true;
            else {
                try {
                    lastWave = firstWave;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("Verteiltes System ist terminiert.");
        Message stopMessage = new Message();
        stopMessage.addHeader("type", "observer_message");
        stopMessage.add("command", "stop");
        for (String participant : participants) {
            sendBlindly(stopMessage, participant);
        }
    }
}
