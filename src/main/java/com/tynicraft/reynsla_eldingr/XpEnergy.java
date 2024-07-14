package com.tynicraft.reynsla_eldingr;

public class XpEnergy {
    private static final int MAX_ENERGY = 1000;
    private int energy;

    public XpEnergy() {
        this.energy = 0;
    }

    public void addEnergy(int amount) {
        this.energy = Math.min(this.energy + amount, MAX_ENERGY);
    }

    public boolean consumeEnergy(int amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
            return true;
        }
        return false;
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }
}