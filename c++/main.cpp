#include"BMPFormatter.h"
#include"PAMFormatter.h"
#include"XMLFormatter.h"
#include<regex>

#include"Image.h"
#include"BOFill.h"
#include"Rectangle.h"
#include"SelectionError.h"

#include"LoadingError.h"
#include"LayerError.h"
#include"ParamsError.h"
#include"CompositeError.h"
#include"SavingError.h"

void PrintMenu() {
	printf("\n/*************************************GLAVNI MENI*************************/\n\n");
	printf("1.Ucitaj projekat\n");
	printf("2.Ucitaj providan sloj\n");
	printf("3.Ucitaj sloj sa slikom\n");
	printf("4.Obrisi sloj sa rednim brojem [0,n-1]*\n");
	printf("5.Aktiviraj sloj za operacije sa rednim brojem [0,n-1]*\n");
	printf("6.Deaktiviraj sloj za operacije sa rednim brojem [0,n-1]*\n");
	printf("7.Postavi vidljivost sloju sa rednim brojem [0,n-1]* tako da ucestvuje u formiranju slike\n");
	printf("8.Postavi vidljivost sloju sa rednim brojem [0,n-1]* tako da ne ucestvuje u formiranju slike\n");
	printf("9.Postavljanje prozirnosti sloju sa rednim brojem [0,n-1]* u opsegu 0-100\n");
	printf("10.Dodaj selekciju\n");
	printf("11.Aktiviraj selekciju sa zadatim imenom\n");
	printf("12.Deaktiviraj selekciju sa zadatim imenom\n");
	printf("13.Obrisi selekciju sa zadatim imenom\n");
	printf("14.Izvrsi operaciju nad slikom\n");
	printf("15.Napravi kompozitnu funkciju\n");
	printf("16.Izvezi funkciju u fajl\n");
	printf("17.Uzvezi funkciju iz fajla\n");
	printf("18.Sacuvaj projekat\n");
	printf("19.Exportuj sliku\n");
	printf("20.KRAJ RADA\n");
	printf("\nNapomene:\n*-rd.br. 0 je sloj koji je najudaljeniji(najdublji) iz ugla korisnika koji posmatra ekran\n");
	printf("\nUnesite opciju > ");
}


int main(int argc, char* argv[]) {
	Image image;
	if (argc >= 3) {
		try {
			//JAVA -primer img/proba.bmp saved/photoshop/files/funs/nothing.fun 255
			std::string name;
			std::string img_path = argv[1];
			std::size_t foundi = img_path.find_last_of("/\\");
			std::string path = img_path.substr(0, foundi);
			std::string fname = img_path.substr(foundi + 1);
			path += "/";
			std::string compositO_path = argv[2];
			std::size_t found = compositO_path.find_last_of("/\\");
			name = compositO_path.substr(found + 1);
			name = name.substr(0, name.size() - 4);
			image.addLayer(img_path);
			image.importCompositeOperation(name, compositO_path);
			int params = image.numofparams(name);
			if (argc == 3 + (params > 0)) {
				int i = 3;
				//std::string cmd = name + " ";
				//while (params) {
				//	cmd += argv[i];
				//	params--;
				//	if (params != 0)cmd + ",";
				//}
				std::string cmdLineArgs = "";
				if (argc > 3)cmdLineArgs = argv[3];
				image.ExecuteOperation(name + " " + cmdLineArgs);
				image.SaveImage(path, fname);
				return 0;
			}
			exit(-1);
			return -1;
		}
		catch (...) {
			exit(-1);
		}
	}
	int option;
	bool running = true;
	while (running) {
		PrintMenu();
		std::cin >> option;
		getchar();
		switch (option) {
			case 1: {
				std::string path;
				std::cout << "Unesite putanju do flavnog fajla projekta .xml> ";
				std::getline(std::cin, path);
				try {
					image.loadImage(path);
				}
				catch (LoadingError& le) {
					std::cout << le;
				}
				break;
			}
			case 2: {
				unsigned w, h;
				if (image.getNumOfLayers()==0) {
					std::cout << "Uesite sirinu sloja > ";
					std::cin >> w;
					std::cout << "Unesite visinu sloja >";
					std::cin >> h;
					getchar();
					try {
						image.addLayer(w, h);
					}
					catch (LoadingError& le) {
						std::cout << le;
					}
				}
				else {
					std::string ch;
					std::cout << "Da li zelite da zadate dimenzije sloju {DA,NE}?";
					std::getline(std::cin, ch);
					if (ch == "DA") {
						std::cout << "Uesite sirinu sloja > ";
						std::cin >> w;
						std::cout << "Unesite visinu sloja >";
						std::cin >> h;
						getchar();
						try {
							image.addLayer(w, h);
						}
						catch (LoadingError& le) {
							std::cout << le;
						}
					}
					else if (ch == "NE") {
						try {
							image.addLayer();
						}
						catch (LoadingError& le) {
							std::cout << le;
						}
					}
				}
				break;
			}
			case 3: {
				std::string path;
				std::cout << "Unesite putanju do slike > ";
				std::getline(std::cin, path);
				try {
					image.addLayer(path);
				}
				catch (LoadingError& le) {
					std::cout << le;
				}
				catch (ReadingError& re) {
					std::cout << re;
				}
				break;
			}
			case 4: {
				std::cout << "Unesite redni broj sloja za brisanje >";
				unsigned n;
				std::cin >> n;
				getchar();
				try {
					image.deleteLayer(n);
				}
				catch (LayerError& le) {
					std::cout << le;
				}
				break;
			}
			case 5: {
				std::cout << "Unesite redni broj sloja za aktiviranje za ucesce u operacijama >";
				unsigned n;
				std::cin >> n;
				getchar();
				try {
					image.setLayerStatus(n, true);
				}
				catch (LayerError& le) {
					std::cout << le;
				}
				break;
			}
			case 6: {
				std::cout << "Unesite redni broj sloja za deaktiviranje za ucesce u operacijama >";
				unsigned n;
				std::cin >> n;
				getchar();
				try {
					image.setLayerStatus(n, false);
				}
				catch (LayerError& le) {
					std::cout << le;
				}
				break;
			}
			case 7: {
				std::cout << "Unesite redni broj sloja za postavljanje vidljivosti na aktivnu >";
				unsigned n;
				std::cin >> n;
				getchar();
				try {
					image.setLayerVisible(n, true);
				}
				catch (LayerError& le) {
					std::cout << le;
				}
				break;
			}
			case 8: {
				std::cout << "Unesite redni broj sloja za postavljanje vidljivosti na neaktivnu >";
				unsigned n;
				std::cin >> n;
				getchar();
				try {
					image.setLayerVisible(n, false);
				}
				catch (LayerError& le) {
					std::cout << le;
				}
				break;
			}
			case 9: {
				std::cout << "Unesite redni broj sloja za postavljanje providnosti >";
				unsigned n;
				std::cin >> n;
				unsigned opacity;
				std::cout << "Unesite providnost za sloj sa rd. brojem "<<n<<" >";
				std::cin >> opacity;
				getchar();
				try {
					image.setLeyerOpacity(n, opacity);
				}
				catch (LayerError& le) {
					std::cout << le;
				}
				break;
			}
			case 10: {
				std::string sname;
				std::cout << "Unesite ime selekcije >";
				std::getline(std::cin, sname);
				std::cout << "Unesite broj pravougaonih selekcija >";
				unsigned n;
				std::cin >> n;
				unsigned sx, sy, sw, sh;
				std::vector<Pravougaonik> rectangles;
				while (n) {
					std::cout << "Unesite Y koordinatu gornnjeg levog ugla >";
					std::cin >> sy;
					std::cout << "Unesite X koordinatu gornnjeg levog ugla >";
					std::cin >> sx;
					std::cout << "Unesite sirinu selekcije >";
					std::cin >> sw;
					std::cout << "Unesite visinu selekcije >";
					std::cin >> sh;
					rectangles.push_back(Pravougaonik(sy, sx, sw, sh));
					n--;
				}
				getchar();
				try {
					image.addSelection(sname,rectangles);
				}
				catch (SelectionError& se) {
					std::cout << se;
				}
				break;
			}
			case 11: {
				std::string sname;
				std::cout << "Unesite ime selekcije za aktivaciju >";
				std::getline(std::cin, sname);
				try {
					image.setSelectionStatus(sname, true);
				}
				catch (SelectionError& se) {
					std::cout << se;
				}
				break;
			}
			case 12: {
				std::string sname;
				std::cout << "Unesite ime selekcije za deaktivaciju >";
				std::getline(std::cin, sname);
				try {
					image.setSelectionStatus(sname, false);
				}
				catch (SelectionError& se) {
					std::cout << se;
				}
				break;
			}
			case 13: {
				std::string sname;
				std::cout << "Unesite ime selekcije za brisanje >";
				std::getline(std::cin, sname);
				try {
					image.deleteSelection(sname);
				}
				catch (SelectionError& se) {
					std::cout << se;
				}
				break;
			}
			case 14: {
				std::string cmd;
				std::cout << "Unesite operaciju sa argumentima (npr. fill 255 0 0; composite 255 0 0,23,2;) >";
				std::getline(std::cin, cmd);
				try {
					image.ExecuteOperation(cmd);
				}
				catch (ParamsError& pe) {
					std::cout << pe;
				}
				break;
			}
			case 15: {
				std::string sname;
				std::cout << "Unesite naziv kompozitne operacije >";
				std::getline(std::cin, sname);
				std::string composite;
				std::cout << "Unesite operacije koje ucestvuju u kompozitnoj (npr. add|sub|subinv ) >";
				std::getline(std::cin, composite);
				try {
					image.createCompositeOperation(sname,composite);
				}
				catch (CompositeError& ce) {
					std::cout << ce;
				}
				break;
			}
			case 16: {
				std::string name;
				std::cout << "Unesite naziv kompozitne operacije >";
				std::getline(std::cin, name);
				std::string path;
				std::cout << "Unesite putanju za cuvanje bez imena fajla (npr. Desktop/) >";
				std::getline(std::cin, path);
				try {
					image.exportCompositeOperation(name, path+name+".fun");
				}
				catch (CompositeError& ce) {
					std::cout << ce;
				}
				break;
			}
			case 17: {
				std::string name;
				std::cout << "Unesite naziv kompozitne operacije koja ce biti uvezena >";
				std::getline(std::cin, name);
				std::string path;
				std::cout << "Unesite putanju za uvoz (npr. Desktop/composite.fun) >";
				std::getline(std::cin, path);
				try {
					image.importCompositeOperation(name, path);
				}
				catch (LoadingError& le) {
					std::cout << le;
				}
				break;
			}
			case 18: {
				std::string name;
				std::cout << "Unesite naziv projekta za cuvanje .xml>";
				std::getline(std::cin, name);
				std::string path="saved\\";
				try {
					image.SaveImage(path,name);
					std::cout << "Projekat ce biti sacuvan na relativnoj putanji u okviru VS projekta >" << path << name << std::endl;
				}
				catch (SavingError& se) {
					std::cout << se;
				}
				break;
			}
			case 19: {
				std::string name;
				std::cout << "Unesite naziv slike za izvoz sa formatom slika[.bmp|.pam] >";
				std::getline(std::cin, name);
				std::string path;
				std::cout << "Unesite putanju za exportovanje slike (npr. Desktop/) >";
				std::getline(std::cin, path);
				try {
					image.SaveImage(path, name);
				}
				catch (SavingError& se) {
					std::cout << se;
				}
				break;
			}
			default: {
				running = false;
				break;
			}
		}

	}

	if (!image.isSaved()) {
		std::cout << "/************UPOZORENJE!!!!!************/\n\tPoslednje izmene nisu sacuvane, da li zelite da eksportujete sliku? {DA,NE} >";
		std::string ch;
		std::getline(std::cin, ch);
		if (ch == "DA") {
			std::string name;
			std::cout << "Unesite naziv slike za izvoz sa formatom slika[.bmp|.pam] >";
			std::getline(std::cin, name);
			std::string path;
			std::cout << "Unesite putanju za exportovanje slike (npr. Desktop/) >";
			std::getline(std::cin, path);
			image.SaveImage(path, name);
		}
	}
	return 0;

	//return 0;
	//int d;
	//std::cout <<l;
	//Operation* op = new BOFill();
	//Pixel p;
	//try {
	//	op->ValidateAndSetParams("   175 45 85   ");
	//	op->Execute(p);
	//}
	//catch (ParamsError& p) {
	//	std::cout << p;
	//}
	//p.setAlpha(255);
	//Image i;
	//i.addLayer("img/proba.bmp");
	//i.SaveImage("img/","slikamoja.bmp");
	//i.SaveImage("img/","slikamoja.pam");
	//i.SaveImage("saved\\", "photoshop.xml");
	//i.loadImage("saved/photoshop/photoshop.xml");

	////l = f->ReadFile("img/konacnaslika.bmp");
	////l.setActive(false);
	////l.setVisible(true);
	////l.setOpacity(100);
	////i.addLayer(l);

	////i.createCompositeOperation("composite", "add|log|grayscale");
	////i.exportCompositeOperation("composite", "composite.fun");
	//i.importCompositeOperation("composite", "composite.fun");
	//i.createCompositeOperation("nothing", "subinv|abs|composite");
	//std::string name = "novaS";
	//std::vector<Pravougaonik> rectangles;
	//Pravougaonik p1(0, 0, 200, 200);
	//rectangles.push_back(p1);
	//i.addSelection(name, rectangles);
	//rectangles.clear();
	////Pravougaonik p3(250, 250, 10, 10);
	////rectangles.push_back(p3);
	////i.addSelection("nova2", rectangles);
	//i.setSelectionStatus(name, true);
	////i.loadImage("saved/", "photoshop");
	//std::cout << "Unesite zeljenu operaciju> ";
	//std::string cmd;
	//std::getline(std::cin, cmd);


	//try {
	//	i.ExecuteOperation(cmd);
	//}
	//catch (...) {
	//	std::cout << "Greska pri izvrsavanju operacije!\n";
	//}
	//i.setSelectionStatus(name, false);
	//std::cout << "Unesite zeljenu operaciju> ";
	//std::getline(std::cin, cmd);
	//try {
	//	i.ExecuteOperation(cmd);
	//}
	//catch (...) {
	//	std::cout << "Greska pri izvrsavanju operacije!\n";
	//}
	//std::cout << "Unesite zeljenu operaciju> ";
	//std::getline(std::cin, cmd);
	//try {
	//	i.ExecuteOperation(cmd);
	//}
	//catch (...) {
	//	std::cout << "Greska pri izvrsavanju operacije!\n";
	//}
	///*l = f->ReadFile("img/film.bmp");
	//l.setOpacity(80);
	//i.addLayer(l);

	//l = f->ReadFile("img/film2.bmp");
	//l.setOpacity(80);
	//i.addLayer(l);

	//l = f->ReadFile("img/film.bmp");
	//l.setOpacity(80);
	//i.addLayer(l);*/



	//Layer&& compresed = i.compressIntoOneLayer();
	//std::cout << compresed;

	//f->SaveFile("img/konacnaslika.bmp", compresed);
	//delete f;
	//delete pf;
	//XMLFormatter* xmlfor = new XMLFormatter();
	//xmlfor->saveImage(i, "saved\\", "photoshop");
	////std::cout << "Unesite komandu >";
	////std::string cmd;
	////std::getline(std::cin,cmd);
	//////std::cout << i.getLayer();
	////i.getLayer().medianatest();
	////std::cout << i.getLayer();
	////i.ExecuteOperation(cmd);
	////std::cout << i.getLayer();
}