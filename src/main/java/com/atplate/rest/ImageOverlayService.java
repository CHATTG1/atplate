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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.awt.Color;

import javax.imageio.ImageIO;
import java.util.List;
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
            BufferedImage bgImage = readImage("/home/preilly/projects/atplate/background.jpg");

            /**
             * Read a foreground image
             */
            //BufferedImage fgImage = readImage("/home/preilly/projects/atplate/foreground.jpg");

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

        /**Create a Graphics  from the background image**/
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
        g.drawImage(fgImage, 27, 27, null);

        g.dispose();
        return bgImage;
    }

    /**
     * This method reads an image from the file
     * @param fileLocation -- > eg. "/home/preilly/projects/atplate/background.jpg"
     * @return BufferedImage of the file read
     */
    public static BufferedImage readImage(String fileLocation) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(fileLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    /**
     * This method writes a buffered image to a file
     * @param img -- > BufferedImage
     * @param fileLocation --> e.g. "/home/preilly/projects/atplate/background.jpg"
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