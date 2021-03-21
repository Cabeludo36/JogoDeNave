package com.cabeludo.mygame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

abstract class Ship {

    //caracteristicas
    float volocidadeMove;  //world units per second
    int shield;

    //posição e dimensão
    Rectangle boundingbox;

    //informação do laser
    float laserLargura, laserAltura;
    float laserVelocidadeMove;
    float tempoEntreTiros;
    float tempoUltimoTiro = 0;

    //graficos
    TextureRegion shipTextureRegion, shieldTextureRegion, laserTextureRegion;

    public Ship(float xCentre, float yCentre,
                float largura, float altura,
                float movementSpeed, int shield,
                float laserWidth, float laserHeight, float laserMovementSpeed,
                float timeBetweenShots,
                TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion,
                TextureRegion laserTextureRegion) {
        this.volocidadeMove = movementSpeed;
        this.shield = shield;
        this.boundingbox = new Rectangle(xCentre - largura / 2,yCentre - altura / 2,largura,altura);
        this.laserLargura = laserWidth;
        this.laserAltura = laserHeight;
        this.laserVelocidadeMove = laserMovementSpeed;
        this.tempoEntreTiros = timeBetweenShots;
        this.shipTextureRegion = shipTextureRegion;
        this.shieldTextureRegion = shieldTextureRegion;
        this.laserTextureRegion = laserTextureRegion;
    }

    public void update(float deltaTempo) {
        tempoUltimoTiro += deltaTempo;
    }

    public boolean canFireLaser() {
        return (tempoUltimoTiro - tempoEntreTiros >= 0);
    }

    public abstract Laser[] fireLasers();

    public boolean intersects(Rectangle outroRetangulo) {
        return boundingbox.overlaps(outroRetangulo);
    }
    public boolean hit(Laser laser) {
        if (shield > 0) {
            shield--;
            return false;
        }
        return true;
    }
    public void translate(float xchage, float ychage) {
        boundingbox.setPosition(boundingbox.x+xchage, boundingbox.y+ychage);
    }
    public void draw(Batch batch) {
        batch.draw(shipTextureRegion, boundingbox.x, boundingbox.y, boundingbox.width, boundingbox.height);
        if (shield > 0) {
            batch.draw(shieldTextureRegion, boundingbox.x, boundingbox.y, boundingbox.width, boundingbox.height);
        }
    }
}
