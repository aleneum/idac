uniform sampler2D src_tex_unit0;
uniform sampler2D src_tex_unit1;
uniform sampler2D src_tex_unit2;
uniform sampler2D src_tex_unit3;

void main(void)
{
    vec2 st = gl_TexCoord[0].st;
    vec4 color0 = texture2D(src_tex_unit0, st);
    vec4 color1 = texture2D(src_tex_unit1, st);
    vec4 color2 = texture2D(src_tex_unit2, st);
    vec4 color3 = texture2D(src_tex_unit3, st);

    gl_FragColor = color0 + color1 + color2 + color3;
}
