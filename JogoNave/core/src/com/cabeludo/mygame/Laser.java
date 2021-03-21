package com.cabeludo.mygame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class Laser {

    //posição e dimensão
    Rectangle boundingbox;

    //caracteristicas fisicas do laser
    float velocidadeMove; //unidades de mundo por segundo

    //graphics
    TextureRegion textureRegion;

    public Laser(float xCentre, float yBottom, float largura, float altura, float movementSpeed, TextureRegion textureRegion) {
        this.boundingbox = new Rectangle(xCentre - largura / 2,yBottom,largura,altura);
        this.velocidadeMove = movementSpeed;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch) {
        batch.draw(textureRegion, boundingbox.x, boundingbox.y, boundingbox.width, boundingbox.height);
    }

/*     public Rectangle getBoundingBox() {
        return boundingbox;
    } */
}
