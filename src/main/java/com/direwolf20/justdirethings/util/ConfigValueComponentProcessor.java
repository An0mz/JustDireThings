package com.direwolf20.justdirethings.util;

import com.direwolf20.justdirethings.setup.Config;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigValueComponentProcessor implements IComponentProcessor {
    private static final Map<String, Supplier<?>> CONFIG_VALUES = Map.of(
        "time_wand.time_wand_max_multiplier", Config.TIME_WAND_MAX_MULTIPLIER::get
    );

    private String rawText;
    private List<String> options;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        this.rawText = variables.get("text").asString();
        this.options = variables.get("config_options")
            .asList()
            .stream()
            .map(IVariable::asString)
            .toList();
    }

    @Override
    public IVariable process(Level level, String key) {
        if (!key.equals("text")) {
            return null;
        }
        String result = this.rawText;
        for (String option : this.options) {
            Supplier<?> configValue = CONFIG_VALUES.get(option);
            result = result.replace("#" + option + "#", configValue != null ? String.valueOf(configValue.get()) : "?");
        }
        return IVariable.wrap(result);
    }
}
