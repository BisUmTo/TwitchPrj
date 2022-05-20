package com.github.gnottero;

import carpet.settings.ParsedRule;
import carpet.settings.Rule;
import carpet.settings.Validator;
import net.minecraft.commands.CommandSourceStack;

public class TwitchProjectSettings {
    @Rule(
            desc = "Invert controls direction",
            category = {}
    )
    public static int invertControlsDirection = 0;

    private static class NoiceSensibility extends Validator<Double> {
        @Override
        public Double validate(CommandSourceStack source, ParsedRule<Double> currentRule, Double newValue, String string) {
            return newValue == -1 || newValue >= 0 && newValue <= 1? newValue : null;
        }

        @Override
        public String description() {
            return "From 0.00 to 1.00, except for -1";
        }
    }

    @Rule(
            desc = "Microphone emit vibration sensibility",
            extra = "-1 disabled, from 0.0 (whisper) up to 1.0 (scream)",
            validate = NoiceSensibility.class,
            category = {}
    )
    public static double microphoneEmitVibration = -1;

    @Rule(
            desc = "Microphone noise will be shown in Actionbar",
            category = {}
    )
    public static boolean microphoneNoiseLogger = false;

    @Rule(
            desc = "Solid collision Entity",
            category = {}
    )
    public static boolean solidCollisionEntity = false;

    @Rule(
            desc = "Upside Down Entities",
            category = {}
    )
    public static boolean upsideDownEntities = false;

}
