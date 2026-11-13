# Adventure Book Application

## Requisitos

- Java 21 JDK
- Maven 3.9.x
- Node.js 24 e npm 11

## 1. Iniciar o backend

Abrir um terminal na pasta do projeto e executar:

```sh
cd backend
mvn spring-boot:run
```

## 2. Iniciar o frontend

Abrir outro terminal na pasta do projeto e executar:

```sh
cd frontend
npm ci
npm start
```

## 3. Abrir a aplicação

Manter os dois terminais abertos e aceder a:

http://localhost:4200

O frontend corre na porta 4200 e encaminha os pedidos da API para o backend, na porta 8080.

Selecionar **Begin Quest** num dos livros jogáveis:

- **The Forest Gate**
- **The Last Light of Blackwater**

Os outros três livros estão desativados porque não cumprem as regras de validação do challenge: contêm secções não finais sem opções ou referências a secções inexistentes. Os motivos são apresentados na biblioteca.

Para parar a aplicação, pressionar `Ctrl+C` nos dois terminais.