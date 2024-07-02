package de.unitrier.s4kibart;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;

public class Observer extends Node {

    private Message m;
    private String[] participants;
    private boolean terminated = false;

    private Pair couldBeTerminated(){
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
        return new Pair(noMessagesSent, noMessagesReceived);
    }

    public Observer(String name, String[] participants) {
        super(name);
        m = new Message();
        m.addHeader("type", "observer_message");
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
            else
                lastWave = firstWave;
        }
        System.out.println("Verteiltes System ist terminiert.");
        System.exit(0);
    }
}
