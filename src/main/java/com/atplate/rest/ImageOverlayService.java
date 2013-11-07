/*
 * Author: Patrick Reilly <preilly@php.net>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atplate.rest;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@Path("/api/image")
@Produces("image/jpg")
public class ImageOverlayService extends BaseService {

    protected String myCodeText;

    public ImageOverlayService(
        @DefaultValue("0") @QueryParam("since") Long since,
        @DefaultValue("0") @QueryParam("until") Long until,
        @DefaultValue("0") @QueryParam("order") String order,
        @DefaultValue("0") @QueryParam("limit") Integer limit,
        @DefaultValue("0") @QueryParam("access_token") String accessToken,
        @DefaultValue("http://atplate.com/") @QueryParam("my_code_text") String myCodeText
    ) {
        super(since, until, order, limit, accessToken);
        this.myCodeText = myCodeText;
    }

    @Path("")
    @GET
    public Response getImageOverlay() {

        ResponseBuilder response = null;
        BufferedImage overlayedImage = null;
        String fileType = "jpg";

        try {
            /**
             * Read a background image
             */
            BufferedImage bgImage = null;
            try {
                bgImage = ImageIO.read(ImageOverlayService.class.getClassLoader().getResourceAsStream("background.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            int size = 145;

            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText,BarcodeFormat.QR_CODE, size, size, hintMap);
            int width = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
 
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, width);
            graphics.setColor(Color.RED);
 
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            // convert BufferedImage to byte array
            byte[] imageInByte;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, fileType, baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();

            // convert byte array back to BufferedImage
            InputStream in = new ByteArrayInputStream(imageInByte);
            BufferedImage fgImage = ImageIO.read(in);

            /**
             * Do the overlay of foreground image on background image
             */
            overlayedImage = overlayImages(bgImage, fgImage);

            /**
             * Write the overlayed image back to file
             */
            if (overlayedImage != null) {
                //writeImage(overlayedImage, "/home/preilly/projects/atplate/overLayedImage.jpg", "JPG");
                System.out.println("Overlay Completed...");
            } else {
                System.out.println("Problem With Overlay...");
            }
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File outputfile = new File("qrcode.jpg");
            ImageIO.write(overlayedImage, fileType, outputfile);
            response = Response.ok((Object) outputfile);
            //response.header("Content-Disposition","attachment; filename=qrcode.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.build();
    }

    /**
     * Method to overlay Images
     *
     * @param bgImage --> The background Image
     * @param fgImage --> The foreground Image
     * @return --> overlayed image (fgImage over bgImage)
     */
    public static BufferedImage overlayImages(BufferedImage bgImage,
            BufferedImage fgImage) {

        /**
         * Doing some preliminary validations.
         * Foreground image height cannot be greater than background image height.
         * Foreground image width cannot be greater than background image width.
         *
         * returning a null value if such condition exists.
         */
        if (fgImage.getHeight() > bgImage.getHeight()
                || fgImage.getWidth() > fgImage.getWidth()) {
            System.out.println("Foreground Image Is Bigger In One or Both Dimensions"
                            + "\nCannot proceed with overlay."
                            + "\n\n Please use smaller Image for foreground");
            return null;
        }

        /**Create a Graphics from the background image**/
        Graphics2D g = bgImage.createGraphics();

        /**Set Antialias Rendering**/
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        /**
         * Draw background image at location (0,0)
         * You can change the (x,y) value as required
         */
        g.drawImage(bgImage, 0, 0, null);

        /**
         * Draw foreground image at location (0,0)
         * Change (x,y) value as required.
         */
        g.drawImage(fgImage, 22, 25, null);

        try {
            InputStream is = ImageOverlayService.class.getClassLoader().getResourceAsStream("04B_03__.TTF");
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            Font sizedFont = font.deriveFont(8f);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(Color.RED);
            g2.setFont(sizedFont);
            g2.drawString("ATPLATE.COM", 75, 30);

            String label = "SOME MORE TEXT";
            double x = 195;
            double y = 170;
            double theta = 270 * java.lang.Math.PI/180;
            /**
             * theta (the rotation angle) is in radians.
             * For vertical text, use 90 * java.lang.Math.PI/180
             * or  270 * java.lang.Math.PI/180
             * depending on whether you want the text top-top-bottom or bottom-to-top
             *
             */

            InputStream is2 = ImageOverlayService.class.getClassLoader().getResourceAsStream("F25_Bank_Printer.ttf");
            Font font2 = Font.createFont(Font.TRUETYPE_FONT, is2);
            Font sizedFont2 = font2.deriveFont(8f);

            // Create a rotation transformation for the font.
            AffineTransform fontAT = new AffineTransform();
            fontAT.rotate(theta);
            Font theDerivedFont = sizedFont2.deriveFont(fontAT);
            Font theDerivedsizedFont = theDerivedFont.deriveFont(13f);

            // set the derived font in the Graphics2D context
            g2.setFont(theDerivedsizedFont);
            g2.setPaint(Color.BLACK);
            // Render a string using the derived font
            g2.drawString(label, (int)x, (int)y);

            // put the original font back
            g2.setFont(font);

            g2.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }

        g.dispose();
        return bgImage;
    }

    /**
     * This method writes a buffered image to a file
     * @param img -- > BufferedImage
     * @param fileLocation --> e.g. "background.jpg"
     * @param extension --> e.g. "jpg","gif","png"
     */
    public static void writeImage(BufferedImage img, String fileLocation,
            String extension) {
        try {
            BufferedImage bi = img;
            File outputfile = new File(fileLocation);
            ImageIO.write(bi, extension, outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}