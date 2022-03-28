package prism4291.henachoko;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;


public class Texture{

    private final int id;

    public Texture(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public static Texture loadTexture(String fileName){

        //load png file
        PNGDecoder decoder;
        try {
            decoder = new PNGDecoder(Texture.class.getResourceAsStream(fileName));



        //create a byte buffer big enough to store RGBA values
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());

        //decode
        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

        //flip the buffer so its ready to read
        buffer.flip();

        //create a texture
        int id = glGenTextures();

        //bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        //tell opengl how to unpack bytes
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //set the texture parameters, can be GL_LINEAR or GL_NEAREST
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //upload texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);

        return new Texture(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Texture drawStrImage(String text){
        int w=text.length()*16;
        int h=16;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics();

        //背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);

        //文字色
        g.setColor(Color.BLACK);
        g.drawString(text, 0, 12);
        g.dispose();

        int[] pixels = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int pixel = pixels[y * img.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
                buffer.put((byte) (pixel & 0xFF)); // Blue component
                buffer.put((byte) (0xFF));
            }
        }

        buffer.flip();

        int id = glGenTextures();

        //bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        //tell opengl how to unpack bytes
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //set the texture parameters, can be GL_LINEAR or GL_NEAREST
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //upload texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);

        return new Texture(id);
    }
}