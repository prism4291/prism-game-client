package prism4291.henachoko;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

import static prism4291.henachoko.PrismGameVariable.WIN;

public class PrismGameWindow {
    public void run() {
        initWindow();
        initRender();
        PrismGameMain mm = new PrismGameMain();
        PrismGameVariable.CURRENT_FPS = 0;
        long secondFromStart = 0;
        long frameFromStart = 0;
        long currentTime;
        long FPSFrom = System.currentTimeMillis();
        while (!GLFW.glfwWindowShouldClose(PrismGameVariable.WIN)) {
            currentTime = System.currentTimeMillis();
            if (currentTime >= FPSFrom + (secondFromStart + 1) * 1000) {
                System.out.println("FPS : " + PrismGameVariable.CURRENT_FPS);
                PrismGameVariable.CURRENT_FPS = 0;
                secondFromStart++;
                //System.out.println(Arrays.toString(Variable.MOUSE_BUTTON));
            }
            if ((currentTime - FPSFrom) * PrismGameVariable.FPS >= frameFromStart * 1000) {
                frameFromStart++;

                if ((currentTime - FPSFrom) * PrismGameVariable.FPS < frameFromStart * 1000) {
                    mm.Main(true);
                    GLFW.glfwSwapBuffers(PrismGameVariable.WIN);
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                    PrismGameVariable.CURRENT_FPS++;
                } else {
                    mm.Main(false);
                }
            }
            GLFW.glfwPollEvents();
        }
        Callbacks.glfwFreeCallbacks(PrismGameVariable.WIN);
        GLFW.glfwDestroyWindow(PrismGameVariable.WIN);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        PrismGameVariable.WIN = GLFW.glfwCreateWindow(PrismGameVariable.WIDTH, PrismGameVariable.HEIGHT, PrismGameVariable.TITLE, PrismGameVariable.FULLSCREEN ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL, MemoryUtil.NULL);
        if (PrismGameVariable.WIN == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the window.");
        }
        GLFW.glfwSetWindowAspectRatio(PrismGameVariable.WIN, 16, 9);
        GLFW.glfwMakeContextCurrent(PrismGameVariable.WIN);
        GLFW.glfwSwapInterval(0);
        mySetCallback();
        GLFW.glfwShowWindow(PrismGameVariable.WIN);
    }

    private void initRender() {
        GL.createCapabilities();
        GL11.glClearColor(0, 0, 0, 0);
        //glEnable(GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void mySetCallback() {
        GLFW.glfwSetWindowSizeCallback(PrismGameVariable.WIN, (win, ww, hh) -> {
            GL11.glViewport(0, 0, ww, hh);
            PrismGameVariable.CURRENT_WIDTH = ww;
            PrismGameVariable.CURRENT_HEIGHT = hh;
        });
        GLFW.glfwSetMouseButtonCallback(WIN, (win, button, action, mods) -> {
            if (button >= 0) {
                switch (action) {
                    case GLFW.GLFW_PRESS:
                        PrismGameVariable.MOUSE_BUTTON[button] = 1;
                        break;
                    case GLFW.GLFW_RELEASE:
                        PrismGameVariable.MOUSE_BUTTON[button] = -PrismGameVariable.MOUSE_BUTTON[button];
                        break;
                }
            }
            //System.out.println(Variable.MOUSE_BUTTON[button]);
        });
        GLFW.glfwSetCursorPosCallback(PrismGameVariable.WIN, (win, xx, yy) -> {
            PrismGameVariable.MOUSE_X = (int) (xx * PrismGameVariable.WIDTH / PrismGameVariable.CURRENT_WIDTH);
            PrismGameVariable.MOUSE_Y = (int) (yy * PrismGameVariable.HEIGHT / PrismGameVariable.CURRENT_HEIGHT);
        });
        GLFW.glfwSetKeyCallback(WIN, new GLFWKeyCallback() {
            @Override
            public void invoke(long win, int key, int code, int action, int mods) {
                //System.out.println(action);
                if (key >= 0) {
                    switch (action) {
                        case GLFW.GLFW_PRESS:
                            PrismGameVariable.KEY_BUTTON[key] = 1;
                            break;
                        case GLFW.GLFW_RELEASE:
                            PrismGameVariable.KEY_BUTTON[key] = -PrismGameVariable.KEY_BUTTON[key];
                            break;
                    }
                }
            }
        });
    }
