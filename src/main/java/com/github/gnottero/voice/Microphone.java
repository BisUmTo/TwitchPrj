package com.github.gnottero.voice;

import com.github.gnottero.TwitchProject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.sound.sampled.*;

public class Microphone implements Runnable {
    public static final int SAMPLE_RATE = 48000;
    public static final int FRAME_SIZE = (SAMPLE_RATE / 1000) * 20;
    public static double LAST_PEAK = 0;
    public final AudioFormat AUDIO_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, 16, 1, 2, SAMPLE_RATE, false);
    private TargetDataLine microphone = null;
    private AudioInputStream is = null;

    @Override
    public void run() {
        try {
            if (microphone == null) {
                microphone = getLine();
                if (microphone == null) throw new Exception("Microphone is null");
                microphone.open(AUDIO_FORMAT, FRAME_SIZE);
                microphone.start();
                is = new AudioInputStream(microphone);
            }


            double peak = 0f;
            if (is.available() > 0) {
                byte[] sample = is.readNBytes(is.available());
                peak = Math.max(peak, Math.abs(media(sample)));
            }
            LAST_PEAK = peak;
        } catch (Exception e) {
            TwitchProject.LOGGER.error(e.getMessage(), e);
            try {
                if (microphone != null) microphone.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            microphone = null;
        }
    }

//    @Override
//    public void run() {
//        TwitchProject.LOGGER.error("CIAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
//        int errors = 0;
//        TargetDataLine microphone = null;
//
//        while (errors <= 10) {
//            try {
//                if (microphone == null) {
//                    microphone = getLine();
//                    if (microphone == null) throw new Exception("Microphone is null");
//                    microphone.open(AUDIO_FORMAT, FRAME_SIZE);
//                    microphone.start();
//                }
//
//
//                double peak = 0f;
//                AudioInputStream is = new AudioInputStream(microphone);
//                if (is.available() > 0) {
//                    byte[] sample = is.readNBytes(FRAME_SIZE);
//                    peak = Math.max(peak, Math.abs(media(sample)));
//                    System.out.println("Peak: " + peak);
//                    is.readAllBytes();
//                }
//                LAST_PEAK = peak;
//                //Thread.sleep(50);
//
//            } catch (InterruptedException ignored) {
//                ignored.printStackTrace();
//                break;
//            } catch (Exception e) {
//                TwitchProject.LOGGER.error(e.getMessage());
//                errors++;
//                microphone = null;
//            }
//        }
//    }

    private TargetDataLine getLine() {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
        try {
            return (TargetDataLine) AudioSystem.getLine(info);
        } catch (Exception e) {
            return null;
        }
    }

    private double media(byte[] bytes) {
        double sum = 0;
        for (byte b : bytes) sum += b;

        return sum / bytes.length;
    }
}
