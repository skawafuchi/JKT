import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

//weird behavior
//when particle # increases, step is run faster
public class Firework {
	short framesToLive = 50;
	int originX, originY;
	Particle[] particles;
	float red, green, blue;
	public float alpha = 1.0f;
	Random rand = new Random();
	Color color;

	public Firework(int x, int y, int particleNum) {
		particles = new Particle[particleNum];
		originX = x;
		originY = y;
		for (int i = 0; i < particleNum; i++) {
			particles[i] = new Particle(originX, originY);
		}
		red = rand.nextFloat();
		green = rand.nextFloat();
		blue = rand.nextFloat();
	}

	void step() {
		for (Particle p : particles) {
			p.step();
		}
	}

	void draw(Graphics g) {
		for (Particle p : particles) {
			p.draw(g);
		}
	}

	private class Particle {
		float deltaX, deltaY, x, y;

		Particle(int originX, int originY) {
			deltaX = rand.nextFloat() - 0.5f;
			deltaY = rand.nextFloat() - 0.5f;
			double norm = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
			deltaX *= (1 / norm) * rand.nextFloat() * 10;
			deltaY *= (1 / norm) * rand.nextFloat() * 10;
			x = originX;
			y = originY;
		}

		void step() {
			if (alpha > 0.0f) {
				x += deltaX;
				y += deltaY;
				deltaY += 0.09f;
				deltaY *= 0.95;
				deltaX *= 0.95;
				alpha -= 0.00005f;
			}
		}

		void draw(Graphics g) {
			g.setColor(new Color(red, green, blue, alpha));
			g.drawOval((int) x, (int) y, 1, 1);
		}
	}
}
