package nl.fl.panels;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import en.lib.drawing.DrawUtils;
import en.lib.input.KeyBinding;
import en.lib.io.IO;
import en.lib.math.MathUtils;
import en.lib.math.Vector;
import en.lib.setup.Panel;
import nl.fl.entities.Player;
import nl.fl.main.MainFL;
import nl.fl.objects.Candle;
import nl.fl.objects.HangLamp;
import nl.fl.objects.Lamp;
import nl.fl.world.Light;
import nl.fl.world.Stage;
import nl.fl.world.World;

public class DrawPanel extends Panel implements MouseListener, MouseMotionListener {
	public static World world;
	public static Player player;
		
	public static double deltaMultiplier = 1;
	private static Point mouseLocation = new Point();
	
	public static boolean DEBUG = false;
	
	private Light aura;
	private Light flashLight;
		
	private BufferedImage underlay = new BufferedImage(MainFL.WIDTH, MainFL.HEIGHT, BufferedImage.TYPE_INT_RGB);
	private BufferedImage overlay = new BufferedImage(MainFL.WIDTH, MainFL.HEIGHT, BufferedImage.TYPE_INT_ARGB);
	
	public static Font bigFont = new Font("Arial", 0, 30);
	public static Rectangle FPSBox = new Rectangle(MainFL.WIDTH/2, 0, MainFL.WIDTH/2, 40);
	public static Rectangle TPSBox = new Rectangle(MainFL.WIDTH/2, 40, MainFL.WIDTH/2, 40);
	public static Rectangle MaxMemoryBox = new Rectangle(MainFL.WIDTH/2, 80, MainFL.WIDTH/2, 40);
	public static Rectangle AllocatedMemoryBox = new Rectangle(MainFL.WIDTH/2, 120, MainFL.WIDTH/2, 40);
	public static Rectangle UsedMemoryBox = new Rectangle(MainFL.WIDTH/2, 160, MainFL.WIDTH/2, 40);
	public static Rectangle FreeMemoryBox = new Rectangle(MainFL.WIDTH/2, 200, MainFL.WIDTH/2, 40);
	public static Rectangle RoomBox = new Rectangle(MainFL.WIDTH/2, 240, MainFL.WIDTH/2, 40);
	public boolean dark = true;
	
	public static Font fullScreenFont = new Font("Arial", 1, 50);
	private boolean finished = false;
	private double endScreenAlpha = 0;
	private long endScreenStartTime = 0;
	
	
	private double worldScreenAlpha = 255;
	private double worldScreenFade = 1;
	private ArrayList<String> worlds = IO.parseStringArray(IO.readFile("resources/WorldsConfig").replaceAll("\n", ""));
	boolean loadingWorld = false;
	
	public DrawPanel() {
		setPreferredSize(new Dimension(MainFL.WIDTH, MainFL.HEIGHT));
		addMouseMotionListener(this);
		addMouseListener(this);
		
		new KeyBinding(KeyEvent.VK_ESCAPE, this, false) {
			public void onAction() {
				MainFL.quit();
			}
		};
		
		new KeyBinding(KeyEvent.VK_SUBTRACT, this, false) {
			public void onAction() {
				DEBUG = !DEBUG;
			}
		};
		
		new KeyBinding(KeyEvent.VK_UP, this, false) {
			public void onAction() {
				if (DEBUG) {
					world.moveStage(0);
				}
			}
		};
		
		new KeyBinding(KeyEvent.VK_RIGHT, this, false) {
			public void onAction() {
				if (DEBUG) {
					world.moveStage(1);
				}
			}
		};
		
		new KeyBinding(KeyEvent.VK_DOWN, this, false) {
			public void onAction() {
				if (DEBUG) {
					world.moveStage(2);
				}
			}
		};
		
		new KeyBinding(KeyEvent.VK_LEFT, this, false) {
			public void onAction() {
				if (DEBUG) {
					world.moveStage(3);
				}
			}
		};
		
		
		world = new World("resources/" + worlds.get(0));
		worlds.remove(0);
		world.postInit();
		
		player = new Player((int)(20*MainFL.widthScale), (int)(30*MainFL.heightScale), this);
		player.moveTo(world.getStart(player));
		
		flashLight = new Light(player.getCenter(), -(int)(Vector.getVectorDirection(player.getCenter(), mouseLocation)), 500, 30, 100);
		aura = new Light(player.getCenter(), 100, 20);
	}
	
	public void tick(double delta) {
		if (!loadingWorld) {
			if (!finished) {
				delta*=deltaMultiplier;
				
				player.tick(delta);
				
				if (player.getCenterX() < 0) {
					player.x += MainFL.WIDTH;
					world.moveStage(3);
				} else if (player.getCenterX() > MainFL.WIDTH) {
					player.x -= MainFL.WIDTH;
					world.moveStage(1);
				}
				
				if (player.getCenterY() < 0) {
					player.y += MainFL.HEIGHT;
					world.moveStage(0);
				} else if (player.getCenterY() > MainFL.HEIGHT) {
					player.y -= MainFL.HEIGHT;
					world.moveStage(2);
				}
				
				if (worlds.isEmpty() && world.currentStage.getY() >= 100) {
					finish();
				} else if (!worlds.isEmpty() && world.currentStage.getY() >= 20) {
					switchWorld("resources/" + worlds.get(0));
					worlds.remove(0);
				}
				
				Point playerCenter = player.getCenter();
				aura.setLocation(playerCenter);
				flashLight.setLocation(playerCenter);
				flashLight.setDirection(-(int)(Vector.getVectorDirection(playerCenter, mouseLocation)));
				
				worldScreenAlpha -= worldScreenFade*delta;
			} else {
				endScreenAlpha+=delta;
				if (System.currentTimeMillis()-endScreenStartTime >= 10000) {
					MainFL.quit();
				}
			}
		}
	}
	
	public void draw(Graphics2D g2) {
		if (!loadingWorld) {
			if (!finished) {
				if (dark) {
					paintTo(underlay.createGraphics());
					overlay = deepCopy(world.currentStage.staticLightMap);
					if (overlay != null) {
						Graphics2D lightGraphics = overlay.createGraphics();
							lightGraphics.setComposite(AlphaComposite.DstOut);
							flashLight.draw(lightGraphics);
							aura.draw(lightGraphics);
						lightGraphics.dispose();
					} else {
						overlay = new BufferedImage(MainFL.WIDTH, MainFL.HEIGHT, BufferedImage.TYPE_INT_ARGB);
						Graphics2D lightGraphics = overlay.createGraphics();
							lightGraphics.setColor(Color.BLACK);
							lightGraphics.fillRect(0, 0, MainFL.WIDTH, MainFL.HEIGHT);
							lightGraphics.setComposite(AlphaComposite.DstOut);
							flashLight.draw(lightGraphics);
							aura.draw(lightGraphics);
						lightGraphics.dispose();
					}
					
					
					g2.setComposite(AlphaComposite.SrcOver);
					g2.drawImage(underlay, 0, 0, null);
					g2.drawImage(overlay, 0, 0, null);
					
					if (worldScreenAlpha > 0) {
						g2.setColor(new Color(0, 0, 0, (int)worldScreenAlpha));
						g2.fillRect(0, 0, MainFL.WIDTH, MainFL.HEIGHT);	
						g2.setColor(new Color(255, 255, 255, (int)worldScreenAlpha));
						g2.setFont(DrawPanel.fullScreenFont);
						DrawUtils.drawStringInBox(world.stageDirectory.replaceAll(Pattern.quote("resources/"), ""), MainFL.getBounds(), g2);
					}
				} else {
					paintTo(g2);
				}
			} else {
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, MainFL.WIDTH, MainFL.HEIGHT);
				
				if (endScreenAlpha < 255) {
					g2.setColor(new Color(255, 255, 255, (int)endScreenAlpha));
				} else {
					g2.setColor(Color.WHITE);
				}
				g2.setFont(fullScreenFont);
				DrawUtils.drawStringInBox("Thanks for playing!", getBounds(), g2);
			}
			
			if (DEBUG) {
				g2.setColor(Color.RED);
				g2.setFont(bigFont);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				DrawUtils.drawStringInBox("FPS="+String.valueOf(FPS), FPSBox, g2);
				DrawUtils.drawStringInBox("TPS="+String.valueOf(TPS), TPSBox, g2);
				DrawUtils.drawStringInBox("Max memory: " + MathUtils.toMegaBytes(Runtime.getRuntime().maxMemory()) + "MB", MaxMemoryBox, g2);
				DrawUtils.drawStringInBox("Allocated memory: " + MathUtils.toMegaBytes(Runtime.getRuntime().totalMemory()) + "MB", AllocatedMemoryBox, g2);
				long usedMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
				DrawUtils.drawStringInBox("Used memory: " + MathUtils.toMegaBytes(usedMemory) + "MB", UsedMemoryBox, g2);
				DrawUtils.drawStringInBox("Free memory: " + MathUtils.toMegaBytes(Runtime.getRuntime().freeMemory()) + "MB", FreeMemoryBox, g2);
				
				DrawUtils.drawStringInBox("(" + world.currentStage.getX() + ", " + world.currentStage.getY() + ")", RoomBox, g2);
			}
		}
	}
	
	private static BufferedImage deepCopy(BufferedImage bi) {
		if (bi == null) {
			return null;
		}
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public void paintTo(Graphics2D graphicsObject) {
		graphicsObject.setColor(Color.WHITE);
		graphicsObject.fillRect(0, 0, MainFL.WIDTH, MainFL.HEIGHT);
		for (Lamp l:world.currentStage.lamps) {
			l.drawLamp(graphicsObject);
		}
		world.draw(graphicsObject);
		player.draw(graphicsObject);
		graphicsObject.dispose();
	}
	
	private void finish() {
		finished = true;
		endScreenStartTime = System.currentTimeMillis();
	} 
	
	private void switchWorld(String worldDirectory) {
		loadingWorld = true;
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		world = new World(worldDirectory);
		worldScreenAlpha = 255;
		world.postInit();
		player.moveTo(world.getStart(player));
		loadingWorld = false;
	}
	
	public void mouseMoved(MouseEvent m) {
		mouseLocation.setLocation(m.getPoint());
	}
	public void mouseDragged(MouseEvent m) {
		mouseLocation.setLocation(m.getPoint());
	}
	public void mousePressed(MouseEvent m) {
		//flashLight.setWidth(10);
		//flashLight.setReach(2000);
	}
	public void mouseReleased(MouseEvent m) {
		//flashLight.setWidth(30);
		//flashLight.setReach(1000);
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
}
