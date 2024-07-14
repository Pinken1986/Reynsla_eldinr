package com.tynicraft.reynsla_eldingr;

public interface XpEnergyConsumer {
    void receiveEnergy(int amount);
    boolean canReceiveEnergy();
    int getEnergyCapacity();
    int getCurrentEnergy();
}