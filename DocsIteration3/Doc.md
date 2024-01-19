## 1. Explicações Globais

Em todos os microservicos foi implementada uma **systemPrivateKey**, responsável pela autorização dos pedido feitos entre instancias
para que não seja possível qualquer indivíduo aceder a pedidos aos quais não foram destinados para os mesmos.
## 2. Explicações Users 
## 3. Explicações Plans
## 4. Explicações Subscriptions
 - Quando um *subscriber* cancela a sua subscrição, todos os dispositivos associados com a mesma são eliminados, para que os mesmos possam ser realocados para outra.
 - Quando um *subscriber* muda de plano, tendo mais dispositivos associados do que o mesmo permite (provavelmente porque mudou de um plano que os permitia), todos os mesmos serão também eliminados, por questões de segurança e simplicidade
 - Um utilizador alterna entre *subscriber* ou *newUser* quando cria/cancela uma subscrição, por questões de segurança para os restantes pedidos
## 5. Explicações Devices
