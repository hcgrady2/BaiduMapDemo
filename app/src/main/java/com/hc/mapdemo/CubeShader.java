package com.hc.mapdemo;

import android.opengl.GLES20;

public class CubeShader {
    int mVertex;
    int mMvpMatrix;
    int mColor;
    int mProgram;

    public CubeShader() {

    }

    String vertexShader = "precision highp float;\n" +
            "        attribute vec3 mVertex;//顶点数组,三维坐标\n" +
            "        attribute vec4 mColor;//颜色数组,三维坐标\n" +
            "        uniform mat4 mMvpMatrix;//mvp矩阵\n" +
            "        varying vec4 color;//\n" +
            "        void main(){\n" +
            "            gl_Position = mMvpMatrix * vec4(mVertex, 1.0);\n" +
            "            color = mColor;\n" +
            "        }";

    String fragmentShader = "//有颜色 没有纹理\n" +
            "        precision highp float;\n" +
            "        varying vec4 color;//\n" +
            "        void main(){\n" +
            "            gl_FragColor = color;\n" +
            "        }";

    public void init() {
        int vertexLocation = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexLocation, vertexShader);
        GLES20.glCompileShader(vertexLocation);

        int fragmentLocation = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentLocation, fragmentShader);
        GLES20.glCompileShader(fragmentLocation);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexLocation);
        GLES20.glAttachShader(mProgram, fragmentLocation);
        GLES20.glLinkProgram(mProgram);

        mVertex  = GLES20.glGetAttribLocation(mProgram, "mVertex");
        mMvpMatrix = GLES20.glGetUniformLocation(mProgram,"mMvpMatrix");
        mColor = GLES20.glGetAttribLocation(mProgram,"mColor");
    }
}