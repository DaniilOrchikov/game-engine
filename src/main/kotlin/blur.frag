uniform sampler2D texture;
uniform vec2 resolution;
uniform float blurSize;

void main() {
    vec2 texelSize = 1.0 / resolution;
    vec4 color = vec4(0.0, 0.0, 0.0, 0.0);

    // Размытие по горизонтали
    color += texture2D(texture, gl_TexCoord[0].xy - 4.0 * texelSize) * 0.05;
    color += texture2D(texture, gl_TexCoord[0].xy - 3.0 * texelSize) * 0.09;
    color += texture2D(texture, gl_TexCoord[0].xy - 2.0 * texelSize) * 0.12;
    color += texture2D(texture, gl_TexCoord[0].xy - texelSize) * 0.15;
    color += texture2D(texture, gl_TexCoord[0].xy) * 0.16;
    color += texture2D(texture, gl_TexCoord[0].xy + texelSize) * 0.15;
    color += texture2D(texture, gl_TexCoord[0].xy + 2.0 * texelSize) * 0.12;
    color += texture2D(texture, gl_TexCoord[0].xy + 3.0 * texelSize) * 0.09;
    color += texture2D(texture, gl_TexCoord[0].xy + 4.0 * texelSize) * 0.05;

    // Размытие по вертикали
    color += texture2D(texture, gl_TexCoord[0].xy - 4.0 * texelSize * vec2(0.0, 1.0)) * 0.05;
    color += texture2D(texture, gl_TexCoord[0].xy - 3.0 * texelSize * vec2(0.0, 1.0)) * 0.09;
    color += texture2D(texture, gl_TexCoord[0].xy - 2.0 * texelSize * vec2(0.0, 1.0)) * 0.12;
    color += texture2D(texture, gl_TexCoord[0].xy - texelSize * vec2(0.0, 1.0)) * 0.15;
    color += texture2D(texture, gl_TexCoord[0].xy) * 0.16;
    color += texture2D(texture, gl_TexCoord[0].xy + texelSize * vec2(0.0, 1.0)) * 0.15;
    color += texture2D(texture, gl_TexCoord[0].xy + 2.0 * texelSize * vec2(0.0, 1.0)) * 0.12;
    color += texture2D(texture, gl_TexCoord[0].xy + 3.0 * texelSize * vec2(0.0, 1.0)) * 0.09;
    color += texture2D(texture, gl_TexCoord[0].xy + 4.0 * texelSize * vec2(0.0, 1.0)) * 0.05;

    gl_FragColor = color;
}
