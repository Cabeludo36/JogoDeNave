package com.cabeludo.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

class GameScreen implements Screen {

    //Tela
    private Camera camera;
    private Viewport viewport;

    //Graficos
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private Texture explosionTexture;

    private TextureRegion[] backgrounds;
    private float backgroundHeight; //Altura em do fundo em unidades do mundo

    private TextureRegion playerShipTextureRegion, playerShieldTextureRegion,
            enemyShipTextureRegion, enemyShieldTextureRegion,
            playerLaserTextureRegion, enemyLaserTextureRegion;

    //Tempo
    private float[] backgroundOffsets = {0, 0, 0, 0};
    private float backgroundMaxScrollingSpeed;
    private float tempoEntreEnemySpawns = 3f;
    private float enemySpawnTime = 0;

    //Parametros de mundo
    private final float WORLD_WIDTH = 72;
    private final float WORLD_HEIGHT = 128;

    //game objects
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipList;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> explosionList;

    private int score = 0;

    //heads-up display
    BitmapFont font;
    float hudVerticalMargin, hudLeftx, hudRigthx, hudCentrex, hudRow1y, hudRow2y, hudSectionWidth;



    GameScreen() {

        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //Inicia o texture atlas
        textureAtlas = new TextureAtlas("images.atlas");

        //Inicia os backgrounds 
        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("Starscape00");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");

        backgroundHeight = WORLD_HEIGHT * 2;
        backgroundMaxScrollingSpeed = (float) (WORLD_HEIGHT) / 4;

        //Inicia as texture regions
        playerShipTextureRegion = textureAtlas.findRegion("playerShip2_blue");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyRed3");
        playerShieldTextureRegion = textureAtlas.findRegion("shield2");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
        enemyShieldTextureRegion.flip(false, true);

        playerLaserTextureRegion = textureAtlas.findRegion("laserBlue03");
        enemyLaserTextureRegion = textureAtlas.findRegion("laserRed03");

        explosionTexture = new Texture("explosao.png");

        //Inicia os game objcts
        playerShip = new PlayerShip(WORLD_WIDTH / 2, WORLD_HEIGHT / 4,
                10, 10,
                48, 6,
                0.4f, 4, 45, 0.5f,
                playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);

        enemyShipList = new LinkedList<>();

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();

        explosionList = new LinkedList<>();

        batch = new SpriteBatch();

        prepareHUD();
    }

    private void prepareHUD(){
        //cira a font do bitmap do font file
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1,1,1,0.3f);
        fontParameter.borderColor = new Color(0,0,0,0.3f);

        font = fontGenerator.generateFont(fontParameter);

        //escala da font do arquivo par caber no mundo
        font.getData().setScale(0.08f);

        //calcula a margin da hud, etc...
        hudVerticalMargin = font.getCapHeight()/2;
        hudLeftx = hudVerticalMargin;
        hudRigthx = WORLD_WIDTH*2/3 - hudLeftx;
        hudCentrex = WORLD_WIDTH/3;
        hudRow1y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2y = hudRow1y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH/3;
    }


    @Override
    public void render(float deltaTempo) {
        batch.begin();

        //scrolling background
        renderBackground(deltaTempo);

        detectInput(deltaTempo);
        playerShip.update(deltaTempo);

        spawnEnemyShips(deltaTempo);

        ListIterator<EnemyShip> enemyShipListIterator =  enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            moveEnemy(enemyShip,deltaTempo);
            enemyShip.update(deltaTempo);
            //enemy ships
            enemyShip.draw(batch);
        }
        //player ship
        playerShip.draw(batch);

        //lasers
        renderLaser(deltaTempo);

        //detecta colisão entre laser e ships
        detectCollisions();


        //desenha lasers
        //remove lazer
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while(iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingbox.y += laser.velocidadeMove*deltaTempo;
            if (laser.boundingbox.y > WORLD_HEIGHT) {
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while(iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingbox.y -= laser.velocidadeMove*deltaTempo;
            if (laser.boundingbox.y + laser.boundingbox.height < 0) {
                iterator.remove();
            }
        }

        //explosões
        renderExplosions(deltaTempo);

        //hud rendering
        updateAndRanderHUD();

        batch.end();
    }

    private void updateAndRanderHUD() {
        //renderiza linhas do topo
        font.draw(batch, "Score", hudLeftx, hudRow1y, hudSectionWidth, Align.left, false);
        font.draw(batch, "Escudo", hudCentrex, hudRow1y, hudSectionWidth, Align.center, false);
        font.draw(batch, "Mortes", hudRigthx, hudRow1y, hudSectionWidth, Align.right, false);
        //renderiza os valores segunda linha
        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudLeftx, hudRow2y, hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.shield), hudCentrex, hudRow2y, hudSectionWidth, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.mortes), hudRigthx, hudRow2y, hudSectionWidth, Align.right, false);
        
    }

    private void spawnEnemyShips(float deltaTempo) {
        enemySpawnTime += deltaTempo;
        if(enemySpawnTime > tempoEntreEnemySpawns){
            enemyShipList.add(new EnemyShip(MainGame.random.nextFloat()*(WORLD_WIDTH-10)+5, WORLD_HEIGHT-5,
                    10, 10,
                    48, 5,
                    0.3f, 5, 50, 0.8f,
                    enemyShipTextureRegion, enemyShieldTextureRegion ,enemyLaserTextureRegion));
            enemySpawnTime -= tempoEntreEnemySpawns;
        }
    }

    private void moveEnemy(EnemyShip enemyShip, float deltaTempo) {
        //estrategia: determinar a distancia max que o ship pode se mover em cada direção
        //checa cada tecla que inporta e move de acordo

        float esqLimite,direLimite,cimaLimite,baixoLimite;
        esqLimite = -enemyShip.boundingbox.x;
        baixoLimite = (float)WORLD_HEIGHT/2 - enemyShip.boundingbox.y;
        direLimite = WORLD_WIDTH - enemyShip.boundingbox.x -enemyShip.boundingbox.width;
        cimaLimite = WORLD_HEIGHT - enemyShip.boundingbox.y -enemyShip.boundingbox.height;

        float xmove = enemyShip.getDirectionVector().x * enemyShip.volocidadeMove*deltaTempo;
        float ymove = enemyShip.getDirectionVector().y * enemyShip.volocidadeMove*deltaTempo;

        if (xmove>0) xmove = Math.min(xmove, direLimite);
        else xmove = Math.max(xmove,esqLimite);

        if (ymove>0) ymove = Math.min(ymove, cimaLimite);
        else ymove = Math.max(ymove, baixoLimite);

        enemyShip.translate(xmove, ymove);
    }

    private void detectInput(float deltaTempo){
        //keyboard input
        //estrategia: determinar a distancia max que o ship pode se mover em cada direção
        //checa cada tecla que inporta e move de acordo

        float esqLimite,direLimite,cimaLimite,baixoLimite;
        esqLimite = -playerShip.boundingbox.x;
        baixoLimite = -playerShip.boundingbox.y;
        direLimite = WORLD_WIDTH - playerShip.boundingbox.x -playerShip.boundingbox.width;
        cimaLimite = (float)WORLD_HEIGHT/2 - playerShip.boundingbox.y -playerShip.boundingbox.height;
    
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)&& direLimite>0) {
            playerShip.translate(Math.min(playerShip.volocidadeMove*deltaTempo, direLimite), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)&& cimaLimite>0) {
            playerShip.translate(0f, Math.min(playerShip.volocidadeMove*deltaTempo, cimaLimite));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)&& esqLimite<0) {
            playerShip.translate(Math.max(-playerShip.volocidadeMove*deltaTempo, esqLimite), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)&& baixoLimite<0) {
            playerShip.translate(0f, Math.max(-playerShip.volocidadeMove*deltaTempo, baixoLimite));
        }
    }

    private void detectCollisions() {
        //para cada player laser, checa se crusa com o enemy ship
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while (enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects((laser.boundingbox))) {
                    //contato com o enemy ship
                    if(enemyShip.hit(laser)){
                        enemyShipListIterator.remove();
                        explosionList.add(new Explosion(explosionTexture, new Rectangle(enemyShip.boundingbox), 0.7f));
                        score+=100;
                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }
        //para cada enemy laser checa se se crusa com o player ship
        laserListIterator = enemyLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.intersects((laser.boundingbox))) {
                //contato com o player ship
                if (playerShip.hit(laser)) {
                    explosionList.add(new Explosion(explosionTexture, new Rectangle(playerShip.boundingbox), 1.6f));
                    playerShip.shield = 10;
                    playerShip.mortes++;
                }
                laserListIterator.remove();
            }
        }
    }
    private void renderExplosions(float deltaTempo){
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()) {
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTempo);
            if (explosion.isFinishid()) {
                explosionListIterator.remove();
            }else{
                explosion.draw(batch);
            }
        }
    }

    private void renderLaser(float deltaTempo){
        //cria novo laser
        //player lasers
        if (playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            for (Laser laser: lasers) {
                playerLaserList.add(laser);
            }
        }

        //enemy lasers
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                for (Laser laser: lasers) {
                    enemyLaserList.add(laser);
                }
            }
        }
    }

    private void renderBackground(float deltaTempo) {

        //atualiza as imagens do background
        backgroundOffsets[0] += deltaTempo * backgroundMaxScrollingSpeed / 8;
        backgroundOffsets[1] += deltaTempo * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[2] += deltaTempo * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[3] += deltaTempo * backgroundMaxScrollingSpeed;

        //desenha as camadas do background
        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            if (backgroundOffsets[layer] > WORLD_HEIGHT) {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer],
                    WORLD_WIDTH, backgroundHeight);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}