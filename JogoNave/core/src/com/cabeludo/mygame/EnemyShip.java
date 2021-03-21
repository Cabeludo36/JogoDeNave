package com.cabeludo.mygame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

class EnemyShip extends Ship {

    Vector2 directionVector;
    float tempoUltimaMudancaDirecao = 0;
    float frequenciaMudancaDirecao = 0.75f;

    public EnemyShip(float xCentre, float yCentre,
                     float width, float height,
                     float movementSpeed, int shield,
                     float laserWidth, float laserHeight,
                     float laserMovementSpeed, float timeBetweenShots,
                     TextureRegion shipTextureRegion,
                     TextureRegion shieldTextureRegion,
                     TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, width, height, movementSpeed, shield, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
        
        directionVector = new Vector2(0, -1);
    }

    public Vector2 getDirectionVector() {
        return directionVector;
    }

    private void vetorDirecaoRandomica(){
        double bearing = MainGame.random.nextDouble()*6.283185; // 0 ate 2*pi
        directionVector.x = (float)Math.sin(bearing);
        directionVector.y = (float)Math.cos(bearing);
    }

    @Override
    public void update(float deltaTempo) {
        super.update(deltaTempo);
        tempoUltimaMudancaDirecao += deltaTempo;
        if (tempoUltimaMudancaDirecao > frequenciaMudancaDirecao) {
            vetorDirecaoRandomica();
            tempoUltimaMudancaDirecao -= frequenciaMudancaDirecao;
        }
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[2];
        laser[0] = new Laser(boundingbox.x + boundingbox.width * 0.18f, boundingbox.y - laserAltura,
                laserLargura, laserAltura,
                laserVelocidadeMove, laserTextureRegion);
        laser[1] = new Laser(boundingbox.x + boundingbox.width * 0.82f, boundingbox.y  - laserAltura,
                laserLargura, laserAltura,
                laserVelocidadeMove, laserTextureRegion);

        tempoUltimoTiro = 0;

        return laser;
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(shipTextureRegion, boundingbox.x, boundingbox.y, boundingbox.width, boundingbox.height);
        if (shield > 0) {
            batch.draw(shieldTextureRegion, boundingbox.x, boundingbox.y-boundingbox.height * 0.2f, boundingbox.width, boundingbox.height);
        }
    }
}