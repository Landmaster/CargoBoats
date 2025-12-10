package com.landmaster.cargoboats.sound;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class MotorboatSoundInstance extends AbstractTickableSoundInstance {
    private final Motorboat motorboat;

    public MotorboatSoundInstance(Motorboat motorboat) {
        super(CargoBoats.MOTORBOAT_SOUND.get(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.motorboat = motorboat;
        looping = true;
        delay = 0;
        volume = 0;
        x = motorboat.getX();
        y = motorboat.getY();
        z = motorboat.getZ();
    }

    @Override
    public boolean canPlaySound() {
        return !motorboat.isSilent();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (motorboat.isRemoved()) {
            this.stop();
        } else {
            x = motorboat.getX();
            y = motorboat.getY();
            z = motorboat.getZ();
            volume = motorboat.rotorSpeed * 0.7f;
        }
    }
}
