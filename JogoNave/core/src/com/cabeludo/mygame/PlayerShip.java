package com.cabeludo.mygame;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

class PlayerShip extends Ship {

    int mortes;

    public PlayerShip(float xCentre, float yCentre,
                      float largura, float altura,
                      float velocidadeMove, int shield,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots,
                      TextureRegion shipTextureRegion,
                      TextureRegion shieldTextureRegion,
                      TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, largura, altura, velocidadeMove, shield, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
        mortes = 0;
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[2];
        laser[0] = new Laser(boundingbox.x + boundingbox.width * 0.07f, boundingbox.y + boundingbox.height * 0.45f,
                laserLargura, laserAltura,
                laserVelocidadeMove, laserTextureRegion);
        laser[1] = new Laser(boundingbox.x + boundingbox.width * 0.93f, boundingbox.y + boundingbox.height * 0.45f,
                laserLargura, laserAltura,
                laserVelocidadeMove, laserTextureRegion);

        tempoUltimoTiro = 0;

        return laser;
    }
}
