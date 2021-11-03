/************** Inicio - Macros **************/
#define lectura_humedad     analogRead(SENSORHUMEDAD)
#define SinHumedad_ConAgua  nivelHumedad() == 0 && nivelAgua() == 0
#define SinHumedad_SinAgua  nivelHumedad() == 0 && nivelAgua() == 1
#define ConHumedad_SinAgua  nivelHumedad() == 1 && nivelAgua() == 0
#define ConHumedad_ConAgua  nivelHumedad() == 1 && nivelAgua() == 1
#define BOMBA_ON            digitalWrite(PULSACION, HIGH)
#define BOMBA_OFF           digitalWrite(PULSACION, LOW)
#define LED_ON              digitalWrite(LED, HIGH)
#define LED_OFF             digitalWrite(LED, LOW)
/************** Fin - Macros **************/


/************** Inicio - Constantes **************/
const uint8_t SENSORHUMEDAD = 27;
const uint8_t ECO           = 26;
const uint8_t TRI           = 25;
const uint8_t LED           = 33;
const uint8_t PULSACION     = 32;
const uint8_t RATIO         = 58.2;
/************** Fin - Constantes **************/




/************** Inicio - Variables **************/
int duracion;
int distancia;
uint8_t porcentaje_agua;
uint8_t porcentaje_humedad;
uint8_t test = 1;
// Máquina de estados finito
enum estados
{
  estado_1,
  estado_2,
  estado_3,
  estado_4
} estado;
/************** Fin - Variables **************/



/************** Inicio - Funciones **************/
void lectura();
uint8_t nivelHumedad();
uint8_t nivelAgua();

/************** Fin - Funciones **************/


void setup() {
  Serial.begin(115200); // Velocidad del puerto serial en baudios
  //Inicializar entradas y salidas
  pinMode(SENSORHUMEDAD, INPUT);
  pinMode(ECO, INPUT); // Eco del ultrasonico para medir la distancia
  pinMode(TRI, OUTPUT); // Activa el ECO
  pinMode(LED, OUTPUT); // LED indicador
  pinMode(PULSACION, OUTPUT); // Pulsacion de la bomba para suministrar agua

}


void loop() 
{
    estado = estado_1; // Estado inicial
    lectura(); 
    delay(1000);
    switch(estado)
    {
        case estado_1:
            BOMBA_OFF;
            LED_ON;
        break;
        
        case estado_2:
            BOMBA_ON;
            LED_OFF;
        break;
        
        case estado_3:
            BOMBA_OFF;
            LED_ON;
        break;
        
        case estado_4:
            BOMBA_OFF;
            LED_OFF;
        break;      
    }

}

void lectura()
{
  // Condiciones de 4 combinaciones para activar cada estado
  if ( SinHumedad_ConAgua ) estado = estado_1;
    else if ( SinHumedad_SinAgua ) estado = estado_2;
      else if ( ConHumedad_SinAgua ) estado = estado_3;
        else if ( ConHumedad_ConAgua ) estado = estado_4;
  delay(20);
  test = test + 1;
}


uint8_t nivelHumedad()
{
  porcentaje_humedad = map(lectura_humedad, 4095, 0, 0, 100); // Mapeo, Regla de 3
  Serial.println("*********************************");
  Serial.println("* Test " + String(test) + "\t\t\t*");
  Serial.println("*\t\t\t\t*");
  Serial.print("*\tNivel de humedad: ");
  Serial.print(porcentaje_humedad);
  Serial.print("%");
  Serial.println("\t*");

  // Rango de niveles
  if ( porcentaje_humedad >= 20 )
  {
    return 1;
  }
  else if ( porcentaje_humedad < 20 )
  {
    return 0;
  }

}

uint8_t nivelAgua()
{
  digitalWrite(TRI, HIGH);
  delay(1);
  digitalWrite(TRI, LOW);
  duracion = pulseIn(ECO, HIGH);
  distancia = duracion / RATIO; // Formula para encontrar la distancia en cm

  // Restricción, máximo de distancia del recipiente
  if( distancia > 11)
  {
    distancia = 11;
  }

  porcentaje_agua = map(distancia, 11, 0, 0, 100); // Mapeo, Regla de 3
  Serial.print("*\tNivel de agua: ");
  Serial.print(porcentaje_agua);
  Serial.print("%");
  Serial.println("\t*");
  Serial.println("*\t\t\t\t*");
  Serial.println("*********************************");
  Serial.println();


  // Rango de niveles
  if ( porcentaje_agua >= 20 )
  {
    return 1;
  }
  else if ( porcentaje_agua < 20 )
  {
    return 0;
  }

}
