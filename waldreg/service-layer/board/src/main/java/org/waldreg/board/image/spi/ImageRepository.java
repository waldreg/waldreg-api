package org.waldreg.board.image.spi;

import java.io.File;

public interface ImageRepository{

    File loadImage(String imageName);

    String getImageById(int id);

}
