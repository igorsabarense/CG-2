package curvas.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class PanelDesenho extends JPanel implements MouseListener, MouseMotionListener {

	// Matriz com a imagem mostrada no panel
	private int[][] matrizImagem = null;

	// numero do ponto para saber se e' o primeiro, ou segundo, ....
	private int numPonto = 0;

	/*
	 * pontos para representar a curva: Bezier: ponto 0: ponto inicial da curva
	 * ponto 1: ponto de controle 1 da curva ponto 2: ponto de controle 2 da curva
	 * ponto 3: ponto final da curva
	 */
	private final Point[] pontosCurva;

	// id da curva a ser desenhada
	private int idCurvaDesenho;
	private final int BEZIER = 1;
	private final int HERMITE = 2;

	// variavel para saber se e' para arrastar um ponto
	private boolean arrastarPonto;
	// variavel para verificar se existe uma curva desenhada na tela
	private boolean existeDesenho;
	// ponto a ser arrastado
	private int pontoArrastado;

	public PanelDesenho() {
		super();

		addMouseListener(this);
		addMouseMotionListener(this);

		this.arrastarPonto = false;
		this.pontoArrastado = -1;
		this.existeDesenho = false;

		this.pontosCurva = new Point[4];
		for (int i = 0; i < 4; i++) {
			this.pontosCurva[i] = new Point(-1, -1);
		}

		matrizImagem = new int[1280][720];
		limparMatriz();
	}

	/**
	 * Desenha os pontos na tela
	 */
	public void desenhaPontos(Graphics2D graphics) {
		for (int i = 0; i < 4; i++) {

			Point ponto = pontosCurva[i];

			if (ponto.getX() != -1.0 && ponto.getY() != -1.0) {

				// pontos de controle de BEZIER como vermelho
				if (this.existeDesenho && this.idCurvaDesenho == BEZIER || this.idCurvaDesenho == HERMITE) {
					if (i == 1 || i == 2) {
						graphics.setColor(Color.RED);
					} else {
						graphics.setColor(Color.BLACK);
					}
				} // fim if

				graphics.fillOval((int) ponto.getX(), (int) ponto.getY(), 5, 5);
			}
		}
	}

	/**
	 * Desenha os pontos individuais da curva do ponto inicial ate o final
	 *
	 * @param x
	 * @param y
	 */
	public void drawLinePoints(int x, int y) {
		if (x >= 0 && x < matrizImagem.length && y >= 0 && y < matrizImagem[0].length) {
			matrizImagem[x][y] = 0;
		}
	}

	/**
	 * Metodo para inicializar as variaveis para os pontos iniciais de Bezier
	 */
	public void inicializarCurvaBezier() {
		this.idCurvaDesenho = this.BEZIER;
		this.existeDesenho = false;

		for (int i = 0; i < 4; i++) {
			this.pontosCurva[i] = new Point(-1, -1);
		}
		this.numPonto = 0;

		limparMatriz();
		repaint();
	}

	public void inicializarCurvaHermite() {
		this.idCurvaDesenho = this.HERMITE;
		this.existeDesenho = false;

		for (int i = 0; i < 4; i++) {
			this.pontosCurva[i] = new Point(-1, -1);
		}
		this.numPonto = 0;

		limparMatriz();
		repaint();

	}

	/**
	 * Limpa a matriz mostrada deixando ela toda branca
	 */
	public void limparMatriz() {

		for (int i = 0; i < this.matrizImagem.length; i++) {
			for (int j = 0; j < this.matrizImagem[0].length; j++) {
				this.matrizImagem[i][j] = 0xFFFFFFFF;
			}
		}
	}

	/**
	 * Transforma uma matriz para um BufferedImage
	 *
	 * @param matriz matriz de pixels RGBA
	 * @return buffer equivalente a matriz
	 */
	public BufferedImage matrixToBuffer(int[][] matriz) {

		BufferedImage image = new BufferedImage(matriz.length, matriz[0].length, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < matriz.length; i++) {
			for (int j = 0; j < matriz[0].length; j++) {
				int pixel = matriz[i][j];

				int alpha = pixel >> 24 & 0XFF;
				int red = pixel >> 16 & 0xFF;
				int green = pixel >> 8 & 0XFF;
				int blue = pixel & 0xFF;

				pixel = (alpha << 24) + (red << 16) + (green << 8) + blue;

				image.setRGB(i, j, pixel);
			}
		}

		return image;
	}

	@Override
	public void paint(Graphics g) {

		Graphics2D graphics = (Graphics2D) g;

		graphics.clearRect(0, 0, this.getWidth(), this.getHeight());

		if (matrizImagem != null) {
			graphics.drawImage(matrixToBuffer(matrizImagem), 0, 0, null);

			desenhaPontos(graphics);
		}
	}

	/**
	 * Metodo para desenhar a curva de Bezier na tela
	 */
	public void plotBezier() {
		double x, y;

		double incr = 0.0001;

		for (double t = 0.0; t <= 1.0; t += incr) {
			x = ((-1 * Math.pow(t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1) * pontosCurva[0].getX()
					+ (3 * Math.pow(t, 3) - 6 * Math.pow(t, 2) + 3 * t + 0) * pontosCurva[1].getX()
					+ (-3 * Math.pow(t, 3) + 3 * Math.pow(t, 2) + 0 * t + 0) * pontosCurva[2].getX()
					+ (1 * Math.pow(t, 3) + 0 * Math.pow(t, 2) + 0 * t + 0) * pontosCurva[3].getX());

			y = ((-1 * Math.pow(t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1) * pontosCurva[0].getY()
					+ (3 * Math.pow(t, 3) - 6 * Math.pow(t, 2) + 3 * t + 0) * pontosCurva[1].getY()
					+ (-3 * Math.pow(t, 3) + 3 * Math.pow(t, 2) + 0 * t + 0) * pontosCurva[2].getY()
					+ (1 * Math.pow(t, 3) + 0 * Math.pow(t, 2) + 0 * t + 0) * pontosCurva[3].getY());

			drawLinePoints((int) x, (int) y);
		}
	}

	public void plotHermite() {
		 double x1 = pontosCurva[0].getX();
         double y1 = pontosCurva[0].getY();
         

         //p0
         double x0 = pontosCurva[1].getX();
         double y0 = pontosCurva[1].getY();
         

         //p3
         double x3 = pontosCurva[2].getX();
         double y3 = pontosCurva[2].getY();
         

         //p2
         double x2 = pontosCurva[3].getX();
         double y2 = pontosCurva[3].getY();
         
         double x, y, t;
         
         int max = (int) ((int) 1.0 / 0.001);

         for (int count = 0; count <= max; count++) {
             
             t = (double) count / (double) max;
             x = ((2 * t * t * t - 3 * t * t + 1) * x0
                     + (t * t * t - 2 * t * t + t) * (x1 - x0)
                     + (-2 * t * t * t + 3 * t * t) * x2
                     + (t * t * t - t * t) * (x3 - x2));
             y = ((2 * t * t * t - 3 * t * t + 1) * y0
                     + (t * t * t - 2 * t * t + t) * (y1 - y0)
                     + (-2 * t * t * t + 3 * t * t) * y2
                     + (t * t * t - t * t) * (y3 - y2));
            

 			drawLinePoints((int) x, (int) y);
         }
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		// se ainda nao completou a curva
		if (this.existeDesenho == false && numPonto < 4) {
			this.pontosCurva[numPonto] = new Point(e.getX(), e.getY());
			numPonto++;

			if (this.existeDesenho == false && numPonto == 4) {
				this.numPonto = 0;
				this.existeDesenho = true;

				if (idCurvaDesenho == BEZIER) {
					plotBezier();
				} else if (idCurvaDesenho == HERMITE) {
					plotHermite();
				}
			}

			repaint();
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {

		int pontoClicado = -1;

		// verifica se um ponto foi clicado
		for (int i = 0; i < 4; i++) {
			Point p = pontosCurva[i];

			// se esse ponto foi clicado
			if (e.getX() >= (p.getX() - 3) && e.getX() <= (p.getX() + 3) && e.getY() >= (p.getY() - 3)
					&& e.getY() <= (p.getY() + 3)) {
				pontoClicado = i;
				break;
			}
		} // fim fro

		if (pontoClicado != -1) {
			// coloca que o usuario esta arrastando esse ponto
			this.arrastarPonto = true;
			this.pontoArrastado = pontoClicado;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// tira o ponto arrastado
		this.arrastarPonto = false;
		this.pontoArrastado = -1;

		if (this.existeDesenho) {

			if (this.idCurvaDesenho == this.BEZIER) {
				limparMatriz();
				plotBezier();
			}
		}

		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// todo
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// todo
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		// se o usuario esta arrastando um ponto
		if (this.arrastarPonto) {
			pontosCurva[this.pontoArrastado].setLocation(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

}