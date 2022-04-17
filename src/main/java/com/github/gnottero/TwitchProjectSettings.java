package com.github.gnottero;

import carpet.settings.ParsedRule;
import carpet.settings.Rule;
import carpet.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;

public class TwitchProjectSettings {
    @Rule(
            desc = "Invert controls direction",
            category = {}
    )
    public static int invertControlsDirection = 0;

    private static class NoiceSensibility extends Validator<Double> {
        @Override
        public Double validate(ServerCommandSource source, ParsedRule<Double> currentRule, Double newValue, String string) {
            return newValue == -1 || newValue >= 0 ? newValue : null;
        }

        @Override
        public String description() {
            return "Cannot be negative, except for -1";
        }
    }

    @Rule(
            desc = "Microphone emit vibration sensibility",
            extra = "-1 disabled, from 2.0 (whisper) up to 10.0 (scream)",
            validate = NoiceSensibility.class,
            category = {}
    )
    public static double microphoneEmitVibration = -1;

}
