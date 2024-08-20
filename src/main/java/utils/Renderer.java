package utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;

public class Renderer {

    private static final HashMap<Integer, Boolean> glCapMap = new HashMap<>();
    public static HashMap<BlockPos, Color> renderMap = new HashMap<>();
    private static final Color DEFAULT_COLOR = new Color(0, 255, 0, 128); // Green with 50% transparency
    private static final float DEFAULT_LINE_WIDTH = 2.0f;

    public static void renderBlockBoundingBox(BlockPos pos, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getRenderManager() == null || mc.theWorld == null) {
            return;
        }

        AxisAlignedBB boundingBox = new AxisAlignedBB(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
        );

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glCapMap.put(GL11.GL_BLEND, GL11.glGetBoolean(GL11.GL_BLEND));
        GL11.glEnable(GL11.GL_BLEND);
        glCapMap.put(GL11.GL_TEXTURE_2D, GL11.glGetBoolean(GL11.GL_TEXTURE_2D));
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glCapMap.put(GL11.GL_DEPTH_TEST, GL11.glGetBoolean(GL11.GL_DEPTH_TEST));
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(DEFAULT_LINE_WIDTH);
        glCapMap.put(GL11.GL_LINE_SMOOTH, GL11.glGetBoolean(GL11.GL_LINE_SMOOTH));
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        Color color = renderMap.getOrDefault(pos, DEFAULT_COLOR);
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);

        GL11.glPushMatrix();
        GL11.glTranslated(-mc.getRenderManager().viewerPosX,
                -mc.getRenderManager().viewerPosY,
                -mc.getRenderManager().viewerPosZ);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        addBoundingBoxVertices(worldRenderer, boundingBox);

        tessellator.draw();
        GL11.glPopMatrix();

        for (Integer cap : glCapMap.keySet()) {
            if (glCapMap.get(cap)) {
                GL11.glEnable(cap);
            } else {
                GL11.glDisable(cap);
            }
        }
        GL11.glDepthMask(true);
    }

    public static void drawLineToBlock(BlockPos blockPos, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getRenderManager() == null || mc.theWorld == null) {
            return;
        }

        double playerX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * partialTicks;
        double playerY = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * partialTicks + mc.thePlayer.getEyeHeight();
        double playerZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * partialTicks;

        double blockX = blockPos.getX() + 0.5;
        double blockY = blockPos.getY() + 0.5;
        double blockZ = blockPos.getZ() + 0.5;

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(DEFAULT_LINE_WIDTH);
        GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);

        GL11.glPushMatrix();
        GL11.glTranslated(-mc.getRenderManager().viewerPosX,
                -mc.getRenderManager().viewerPosY,
                -mc.getRenderManager().viewerPosZ);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        worldRenderer.pos(playerX, playerY, playerZ).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        worldRenderer.pos(blockX, blockY, blockZ).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();

        tessellator.draw();
        GL11.glPopMatrix();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private static void addBoundingBoxVertices(WorldRenderer buffer, AxisAlignedBB bb) {
        float r = 0.0f, g = 1.0f, b = 0.0f;

        buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, 1.0f).endVertex();

        buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, 1.0f).endVertex();

        buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, 1.0f).endVertex();
    }
}
