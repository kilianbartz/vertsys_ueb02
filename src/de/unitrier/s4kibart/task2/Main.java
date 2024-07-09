package de.unitrier.s4kibart.task2;

import org.oxoo2a.sim4da.Simulator;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        final int numParticipants = 1000;
        Simulator sim = Simulator.getInstance();
        Actor[] actors = new Actor[numParticipants];
        ArrayList<String> nodeNames = new ArrayList<>();
        for (int i = 0; i < numParticipants; i++) {
            String nodeName = "Actor_" + i;
            nodeNames.add(nodeName);
            actors[i] = new Actor(nodeName);
        }
        for (int i = 0; i < numParticipants; i++) {
            String[] otherParticipants = new String[numParticipants-1];
            int offset = 0;
            for (int j = 0; j < numParticipants; j++){
                String name = nodeNames.get(j);
                if (name.equals(actors[i].getName())){
                    offset++;
                    continue;
                }
                otherParticipants[j-offset] = name;
            }
            actors[i].setOtherParticipants(otherParticipants);
        }
        sim.simulate(6);
        sim.shutdown();
        System.exit(0);
        // dadurch, dass die Aktorenthreads alle in einer Endlosschleife sind, können sie nicht gejoint werden und das Programm terminiert nicht regulär
    }
}
