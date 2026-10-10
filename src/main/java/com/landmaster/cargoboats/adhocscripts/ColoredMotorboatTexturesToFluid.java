package com.landmaster.cargoboats.adhocscripts;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ColoredMotorboatTexturesToFluid {
    private static final String[] motorboatColors = {
            "black",
            "brown",
            "cyan",
            "gray",
            "green",
            "light_blue",
            "light_gray",
            "lime",
            "magenta",
            "orange",
            "pink",
            "purple",
            "red",
            "white",
            "yellow",
    };
    
    public static void main(String[] args) throws IOException {
        var fluidMotorboatTemplate = ImageIO.read(
                new File("src/main/resources/assets/cargoboats/textures/entity/fluid_motorboat.png")
        );
        for (var color: motorboatColors) {
            var image = ImageIO.read(
                    new File(String.format("src/main/resources/assets/cargoboats/textures/entity/motorboat_%s.png", color))
            );
            var graphics = image.createGraphics();
            graphics.setBackground(new Color(0, 0, 0, 0));
            graphics.drawImage(fluidMotorboatTemplate.getSubimage(0, 46, 64, 18), 0, 46, null);
            graphics.drawImage(image.getSubimage(24, 8, 8, 4), 25, 8, null);
            graphics.drawImage(image.getSubimage(8, 7, 8, 5), 16, 7, null);
            graphics.clearRect(8, 7, 8, 5);
            graphics.clearRect(24, 8, 1, 4);

            ImageIO.write(image, "png",
                    new File(String.format("src/main/resources/assets/cargoboats/textures/entity/fluid_motorboat_%s.png", color))
            );
        }
    }
}
