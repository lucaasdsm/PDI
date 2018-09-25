package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PDI {
	
	public static Image escalaDeCinza(Image imagem, double red, double green, double blue){
		try {
			int w = (int)imagem.getWidth();
			int h = (int)imagem.getHeight();
			double soma = (red + green + blue);
			
			PixelReader pr = imagem.getPixelReader();
			WritableImage wi = new WritableImage(w,h);
			PixelWriter pw = wi.getPixelWriter();
			
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color prevColor = pr.getColor(i, j);
					if (soma == 100) {
						double mediaP = (prevColor.getBlue()*(blue/100) + prevColor.getGreen()*(green/100) + prevColor.getRed()*(red/100));
						Color newColor = new Color(mediaP, mediaP, mediaP, prevColor.getOpacity()); 
						pw.setColor(i, j, newColor);
						
					}else{
						double mediaA = (prevColor.getBlue() + prevColor.getGreen() + prevColor.getRed())/3;
						Color newColor = new Color(mediaA, mediaA, mediaA, prevColor.getOpacity());
						pw.setColor(i, j, newColor);
					}
				}
			}
			return wi;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Image limiarizacao(Image imagem, double limiar){
		try {
			int w = (int)imagem.getWidth();
			int h = (int)imagem.getHeight();
			
			PixelReader pr = imagem.getPixelReader();
			WritableImage wi = new WritableImage(w,h);
			PixelWriter pw = wi.getPixelWriter();
			
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color prevColor = pr.getColor(i, j);
					Color newColor;
					if (prevColor.getRed() >= limiar) {
						newColor = new Color(1, 1, 1, prevColor.getOpacity());
					}else{
						newColor = new Color(0, 0, 0, prevColor.getOpacity());
					}
					pw.setColor(i, j, newColor);
					}
			}
			return wi;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image negativo(Image imagem){
		try {
			int w = (int)imagem.getWidth();
			int h = (int)imagem.getHeight();
			
			PixelReader pr = imagem.getPixelReader();
			WritableImage wi = new WritableImage(w,h);
			PixelWriter pw = wi.getPixelWriter();
			
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color prevColor = pr.getColor(i, j);

					double color1 = (1 - (prevColor.getRed()));
					double color2 = (1 - (prevColor.getGreen()));
					double color3 = (1 - (prevColor.getBlue()));
					
					Color newColor = new Color(color1, color2, color3, prevColor.getOpacity());
					pw.setColor(i, j, newColor);
					}
			}
			return wi;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image ruido(Image imagem, int tipoVizinho){
		try {
			int w = (int)imagem.getWidth();
			int h = (int)imagem.getHeight();
			
			PixelReader pr = imagem.getPixelReader();
			WritableImage wi = new WritableImage(w, h);
			PixelWriter pw = wi.getPixelWriter();
			
			for (int i = 1; i < (w - 1); i++) {
				for (int j = 1; j < (h -1); j++) {
					Color prevColor = pr.getColor(i, j);
					
					Pixel p = new Pixel(prevColor.getRed(), prevColor.getGreen(), prevColor.getBlue(), i, j );
					
					if (tipoVizinho == Constantes.VIZINHOC){
						p.vizC = criaVizinhoC(imagem, p, i, j);
						
						double medianaR = mediana(p.vizC, 1);
						double medianaG = mediana(p.vizC, 2);
						double medianaB = mediana(p.vizC, 3);
						
						Color newColor = new Color(medianaR, medianaG, medianaB, prevColor.getOpacity()); 
						pw.setColor(i, j, newColor);
					}
					
					if (tipoVizinho == Constantes.VIZINHOX) {
						p.vizX = criaVizinhoX(imagem, p, i, j);
						
						double medianaR = mediana(p.vizX, 1);
						double medianaG = mediana(p.vizX, 2);
						double medianaB = mediana(p.vizX, 3);		

						Color newColor = new Color(medianaR, medianaG, medianaB, prevColor.getOpacity()); 
						pw.setColor(i, j, newColor);
					}

					if (tipoVizinho == Constantes.VIZINHO3X3) {
						p.vizC = criaVizinhoC(imagem, p, i, j);
						p.vizX = criaVizinhoX(imagem, p, i, j);

						p.viz3.addAll(p.vizX);
						p.viz3.addAll(p.vizC);	
						
						double medianaR = mediana(p.viz3, 1);
						double medianaG = mediana(p.viz3, 2);
						double medianaB = mediana(p.viz3, 3);		

						Color newColor = new Color(medianaR, medianaG, medianaB, prevColor.getOpacity()); 
						pw.setColor(i, j, newColor);
					}
				}
			}
			return wi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static double mediana(ArrayList<Pixel> p, int canal){
		
		double v[] = new double[p.size()];
		ArrayList<Pixel> ordenado = new ArrayList<>();
		
		if(canal == Constantes.CANALR){
			ordenado = ordenaRed(p);
			for (int i = 0; i < p.size(); i++) {
				v[i] = ordenado.get(i).r;
			}
		}
		if(canal == Constantes.CANALG){
			ordenado = ordenaGreen(p);
			for (int i = 0; i < p.size(); i++) {
				v[i] = ordenado.get(i).g;
			}
		}
		if(canal == Constantes.CANALB){
			ordenado = ordenaBlue(p);
			for (int i = 0; i < p.size(); i++) {
				v[i] = ordenado.get(i).b;
			}
		}
		return v[v.length/2];
	}
	
	public static ArrayList<Pixel> criaVizinhoX(Image imagem, Pixel p, int x, int y){
		ArrayList<Pixel> tmp = new ArrayList<>();
		PixelReader pr = imagem.getPixelReader();

		Color cor1 = pr.getColor(x-1, y+1);
		Pixel p1 = new Pixel(cor1.getRed(), cor1.getGreen(), cor1.getBlue(), x-1, y+1);
		tmp.add(p1);
		
		Color cor2 = pr.getColor(x+1, y-1);
		Pixel p2 = new Pixel(cor2.getRed(), cor2.getGreen(), cor2.getBlue(), x+1, y-1);
		tmp.add(p2);
		
		Color cor3 = pr.getColor(x-1, y-1);
		Pixel p3 = new Pixel(cor3.getRed(), cor3.getGreen(), cor3.getBlue(), x-1, y-1);
		tmp.add(p3);
		
		Color cor4 = pr.getColor(x+1, y+1);
		Pixel p4 = new Pixel(cor4.getRed(), cor4.getGreen(), cor4.getBlue(), x+1, y+1);
		tmp.add(p4);
		
		tmp.add(p);
		
		return tmp;
	}
	
	public static ArrayList<Pixel> criaVizinhoC(Image imagem, Pixel p, int x, int y) {
		ArrayList<Pixel> tmp = new ArrayList<>();
		PixelReader pr = imagem.getPixelReader();

		Color cor1 = pr.getColor(x, y-1);
		Pixel p1 = new Pixel(cor1.getRed(), cor1.getGreen(), cor1.getBlue(), x, y-1);
		tmp.add(p1);
		
		Color cor2 = pr.getColor(x, y+1);
		Pixel p2 = new Pixel(cor2.getRed(), cor2.getGreen(), cor2.getBlue(), x, y+1);
		tmp.add(p2);
		
		Color cor3 = pr.getColor(x-1, y);
		Pixel p3 = new Pixel(cor3.getRed(), cor3.getGreen(), cor3.getBlue(), x-1, y);
		tmp.add(p3);
		
		Color cor4 = pr.getColor(x+1, y);
		Pixel p4 = new Pixel(cor4.getRed(), cor4.getGreen(), cor4.getBlue(), x+1, y);
		tmp.add(p4);
		
		tmp.add(p);
		
		return tmp;	
	}
	
	public static ArrayList<Pixel> ordenaRed(ArrayList<Pixel> tmp){
		Collections.sort(tmp, new Comparator<Pixel>() {
			@Override
			public int compare(Pixel p1, Pixel p2){
//				Pixel pixel1 = (Pixel)p1;
//				Pixel pixel2 = (Pixel)p2;
				return ((int)p1.r - (int)p2.r);
			}
		});
		return tmp;
	}
	
	public static ArrayList<Pixel> ordenaGreen(ArrayList<Pixel> tmp){
		Collections.sort(tmp, new Comparator<Pixel>() {
			@Override
			public int compare(Pixel p1, Pixel p2){
				return ((int)p1.g - (int)p2.g);
			}
		});
		return tmp;
	}
	
	public static ArrayList<Pixel> ordenaBlue(ArrayList<Pixel> tmp){
		Collections.sort(tmp, new Comparator<Pixel>() {
			@Override
			public int compare(Pixel p1, Pixel p2){
				return ((int)p1.b - (int)p2.b);
			}
		});
		return tmp;
	}
	
	public static Image adicao(Image img1, Image img2, String text, String text2) {

		double p1 = Double.parseDouble(text)/100;
		double p2 = Double.parseDouble(text2)/100;

		try {
			int w1 = (int)img1.getWidth();
			int h1 = (int)img1.getHeight();
			int w2 = (int)img2.getWidth();
			int h2 = (int)img2.getHeight();
			int w;
			int h;
			
			if (w1 <= w2) {
				w = w1;
			} else {
				w = w2;
			}
			
			if (h1 <= h2) {
				h = h1;
			} else {
				h = h2;
			}
			
			PixelReader pr1 = img1.getPixelReader();
			PixelReader pr2 = img2.getPixelReader();
			
			WritableImage wi = new WritableImage(w2,h2);
			PixelWriter pw = wi.getPixelWriter();
			
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color prevColor1 = pr1.getColor(i, j);
					Color prevColor2 = pr2.getColor(i, j);

					double color1 = (prevColor2.getRed()*p2 + prevColor1.getRed()*p1);
					double color2 = (prevColor2.getGreen()*p2 + prevColor1.getGreen()*p1);
					double color3 = (prevColor2.getBlue()*p2 + prevColor1.getBlue()*p1);
					
					Color newColor = new Color(color1, color2, color3, prevColor1.getOpacity());
					pw.setColor(i, j, newColor);
					}
			}
			return wi;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public static Image subtracao(Image img1, Image img2) {

		try {
			int w1 = (int)img1.getWidth();
			int h1 = (int)img1.getHeight();
			int w2 = (int)img2.getWidth();
			int h2 = (int)img2.getHeight();
			int w;
			int h;
			
			if (w1 <= w2) {
				w = w1;
			} else {
				w = w2;
			}
			
			if (h1 <= h2) {
				h = h1;
			} else {
				h = h2;
			}
			
			PixelReader pr1 = img1.getPixelReader();
			PixelReader pr2 = img2.getPixelReader();
			
			WritableImage wi = new WritableImage(w2,h2);
			PixelWriter pw = wi.getPixelWriter();
			
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color prevColor1 = pr1.getColor(i, j);
					Color prevColor2 = pr2.getColor(i, j);

					double color1 = (prevColor1.getRed() - prevColor2.getRed());
					double color2 = (prevColor1.getGreen() - prevColor2.getGreen());
					double color3 = (prevColor1.getBlue() - prevColor2.getBlue());
					
					if (color1 < 0) {
						color1 = 0;
					}
					if (color2 < 0) {
						color2 = 0;
					}
					if (color3 < 0) {
						color3 = 0;
					}
					Color newColor = new Color(color1, color2, color3, prevColor1.getOpacity());
					pw.setColor(i, j, newColor);
					}
			}
			return wi;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Image moldura(Image img1, int x1, int x2, int y1, int y2) {

		try {
			int w1 = (int)img1.getWidth();
			int h1 = (int)img1.getHeight();
			
			PixelReader pr1 = img1.getPixelReader();
			
			WritableImage wi = new WritableImage(w1,h1);
			PixelWriter pw = wi.getPixelWriter();
			
			for (int i = 0; i < w1; i++) {
				for (int j = 0; j < h1; j++) {
					Color prevColor1 = pr1.getColor(i, j);
					pw.setColor(i, j, prevColor1);
				}
			}
			for (int k = x1; k < x2; k++) {
				Color prevColor1 = pr1.getColor(k, y1);
				if (k <= x2) {
					double color1 = (25/255);
					double color2 = (1);
					double color3 = (40/255);
					Color newColor = new Color(color1, color2, color3, prevColor1.getOpacity());
					pw.setColor(k, y1, newColor);
				}
			}
			for (int k = x1; k < x2; k++) {
				Color prevColor1 = pr1.getColor(k, y2);
				if (k <= x2) {
					double color1 = (25/255);
					double color2 = (1);
					double color3 = (40/255);
					Color newColor = new Color(color1, color2, color3, prevColor1.getOpacity());
					pw.setColor(k, y2, newColor);
				}
			}
			for (int k = y1; k < y2; k++) {
				Color prevColor1 = pr1.getColor(x1, k);
				if (k <= y2) {
					double color1 = (25/255);
					double color2 = (1);
					double color3 = (40/255);
					Color newColor = new Color(color1, color2, color3, prevColor1.getOpacity());
					pw.setColor(x1, k, newColor);
				}
			}			
			for (int k = y1; k < y2; k++) {
				Color prevColor1 = pr1.getColor(x2, k);
				if (k <= y2) {
					double color1 = (25/255);
					double color2 = (1);
					double color3 = (40/255);
					Color newColor = new Color(color1, color2, color3, prevColor1.getOpacity());
					pw.setColor(x2, k, newColor);
				}
			}	
			return wi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image rotate90(Image img1) {

		try {
			int w1 = (int)img1.getWidth();
			int h1 = (int)img1.getHeight();

			PixelReader pr1 = img1.getPixelReader();
			
			WritableImage wi = new WritableImage(h1,w1);
			PixelWriter pw = wi.getPixelWriter();
			
			int m = w1 - 1;
			for (int i = 0; i < w1; i++) {
				int n = h1;
				for (int j = 0; j < h1; j++) {
					Color prevColor = pr1.getColor(i, j);

					double color1 = (prevColor.getRed());
					double color2 = (prevColor.getGreen());
					double color3 = (prevColor.getBlue());
					
					Color newColor = new Color(color1, color2, color3, prevColor.getOpacity());
					
					pw.setColor(h1 - n, m, newColor);
					n--;
				}
				m--;
			}
			return wi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void getGrafico(Image img,BarChart<String, Number> grafico){
		CategoryAxis eixoX = new CategoryAxis();
		NumberAxis eixoY = new NumberAxis();
	    eixoX.setLabel("Intensidade");       
	    eixoY.setLabel("Valor");
	    XYChart.Series vlr = new XYChart.Series();
	    vlr.setName("Intensidade");
	    
	    int[] hist = histogramaUnico(img);
	    
	    for (int i=0; i<hist.length; i++) {
	    	vlr.getData().add(new XYChart.Data(i+"", hist[i]/1000));
		}
	    grafico.getData().addAll(vlr);
}

	private static int[] histogramaUnico(Image img1) {
		img1 = escalaDeCinza(img1, 0, 0, 0);
		int[] tmp = new int[256];
		
		try {
			int w1 = (int)img1.getWidth();
			int h1 = (int)img1.getHeight();

			PixelReader pr1 = img1.getPixelReader();
			
			for (int i = 0; i < w1; i++) {
				for (int j = 0; j < h1; j++) {
					Color prevColor = pr1.getColor(i, j);
					
					int m1 = (int) (((prevColor.getBlue() + prevColor.getGreen() + prevColor.getRed()) / 3) * 255);
					tmp[(int) m1] = tmp[(int) m1] + 1;
				}
			}
		} catch (Exception e) {  
			e.printStackTrace();
		}
		return tmp;
	}
	
	public static int[] histogramaAcumulado(int[] hist){
		int[] tmp = new int[hist.length];
		int vl = hist[0];
		for(int i=0; i<hist.length-1; i++){
			tmp[i] = vl;
			vl += hist[i+1];
		}
		return tmp;
}
	
	public static Image equalizacaoHistograma(Image img){
	    int w = (int)img.getWidth();
	    int h = (int)img.getHeight();
    	PixelReader pr = img.getPixelReader();
    	WritableImage wi = new WritableImage(w,h);
    	PixelWriter pw = wi.getPixelWriter();
    	
    	int[] hR = histograma(img, 1);
		int[] hG = histograma(img, 2);
		int[] hB = histograma(img, 3);
		int[] histAcR = histogramaAcumulado(hR);
		int[] histAcG = histogramaAcumulado(hB);
		int[] histAcB = histogramaAcumulado(hG);
		
    	for (int i=1; i < w; i++){
    		for (int j=1; j < h; j++){	
    			Color oldCor = pr.getColor(i,j);
    			
    			double acR = histAcR[(int)(oldCor.getRed()*255)];
    			double acG = histAcG[(int)(oldCor.getGreen()*255)];
    			double acB = histAcB[(int)(oldCor.getBlue()*255)];
    			double n = w*h;
    			double pxR = ((255-1)/n)*acR;
    			double pxG = ((255-1)/n)*acG;
    			double pxB = ((255-1)/n)*acB;
    			double corR = pxR/255;
    			double corG = pxG/255;
    			double corB = pxB/255;
    			
    			Color newCor = new Color(corR,corG,corB,oldCor.getOpacity());
    			pw.setColor(i, j, newCor);
    		}
    	}
    	return wi;
	}
	
	private static int[] histograma(Image img1, int canal) {
		
		PixelReader pr = img1.getPixelReader();
		int w1 = (int)img1.getWidth();
		int h1 = (int)img1.getHeight();
		int[] vetor = new int[256];
	
			for (int i = 0; i < w1; i++) {
				for (int j = 0; j < h1; j++) {
					Color prevColor = pr.getColor(i, j);
					
					if (canal == Constantes.CANALR) {
						vetor[(int)(prevColor.getRed() * 255)]++;
					} else if (canal == Constantes.CANALG) {
						vetor[(int)(prevColor.getGreen() * 255)]++;
					}else if (canal == Constantes.CANALB) {
						vetor[(int)(prevColor.getBlue() * 255)]++;
					}
				}
			}
		return vetor;
	}
}