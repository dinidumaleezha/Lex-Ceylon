package com.dinidu.lexceylon.Class;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.graphics.Shader;

public class ShaderSpan extends CharacterStyle implements UpdateAppearance {

    private final Shader shader;

    public ShaderSpan(Shader shader) {
        this.shader = shader;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setShader(shader);
    }
}
