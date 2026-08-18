# 📚 Sistema Biblioteca Spring

API REST desenvolvida em **Java** com **Spring Boot** para gerenciamento de uma biblioteca.

O sistema permite o cadastro de livros e usuários, controle de empréstimos, autenticação utilizando JWT e gerenciamento de permissões entre usuários comuns e administradores.

> 🚧 **Projeto em desenvolvimento**
>
> Este projeto está sendo desenvolvido como parte dos meus estudos em desenvolvimento Backend com Java e Spring Boot. Novas funcionalidades e melhorias serão adicionadas continuamente.

---

# ✨ Objetivos do projeto

Este projeto foi desenvolvido com o objetivo de praticar conceitos importantes do desenvolvimento Backend, como:

* Desenvolvimento de APIs REST
* Java
* Spring Boot
* Spring Security
* Autenticação JWT
* Spring Data JPA
* Hibernate
* Arquitetura em camadas
* Tratamento de exceções
* Paginação e filtros
* Testes unitários utilizando JUnit 5 e Mockito
* Boas práticas de organização de código

---

# 🚀 Tecnologias utilizadas

* Java
* Spring Boot
* Spring Web
* Spring Security
* Spring Data JPA
* Hibernate
* JWT
* Maven
* JUnit 5
* Mockito

---

# 📌 Funcionalidades

## 📚 Livros

* Cadastro de livros
* Atualização de livros
* Exclusão de livros
* Listagem paginada
* Pesquisa por título
* Filtro por disponibilidade

---

## 👤 Usuários

* Cadastro de usuários
* Login
* Atualização de dados
* Exclusão de conta
* Listagem de usuários
* Controle de permissões (ADMIN e USUÁRIO)

---

## 📖 Empréstimos

* Empréstimo de livros
* Devolução de livros
* Controle automático do status do livro

---

# 🏛 Arquitetura

O projeto segue uma arquitetura em camadas, separando as responsabilidades da aplicação.

```text
Cliente
    │
    ▼
Controller
    │
    ▼
Service
    │
    ▼
Repository
    │
    ▼
Banco de Dados
```

---

# 📂 Estrutura do projeto

```text
src
├── configuration
├── controller
├── dto
├── exception
├── model
├── repository
└── service
```

---

# 📚 Endpoints

## Livros

| Método | Endpoint       |
| ------ | -------------- |
| GET    | `/livros`      |
| POST   | `/livros`      |
| PUT    | `/livros/{id}` |
| DELETE | `/livros/{id}` |

---

## Empréstimos

| Método | Endpoint                 |
| ------ | ------------------------ |
| GET    | `/emprestimos`           |
| POST   | `/emprestimos/emprestar` |
| POST   | `/emprestimos/devolver`  |

---

## Usuários

| Método | Endpoint                |
| ------ | ----------------------- |
| POST   | `/usuarios/criar_conta` |
| POST   | `/usuarios/login`       |
| PUT    | `/usuarios/atualizar`   |
| DELETE | `/usuarios/excluir`     |
| GET    | `/usuarios/listar`      |

---

# 🧪 Testes

O projeto possui testes unitários desenvolvidos utilizando **JUnit 5** e **Mockito**.

Atualmente foram implementados testes para:

* ✅ LivrosService
* ✅ UsuariosService
* ✅ EmprestimosService

Os testes contemplam:

* Cenários de sucesso
* Validação das regras de negócio
* Tratamento de exceções
* Controle de permissões
* Verificação das interações com os repositórios

---

# ▶️ Como executar

Clone o repositório:

```bash
git clone https://github.com/bieleiji/sistema-biblioteca-spring.git
```

Entre na pasta do projeto:

```bash
cd sistema-biblioteca-spring
```

Execute a aplicação:

```bash
./mvnw spring-boot:run
```

Ou, caso utilize Maven instalado:

```bash
mvn spring-boot:run
```

---

# 📈 Próximas melhorias

* Documentação da API
* Testes de integração
* Docker
* Deploy da aplicação
* Pipeline CI/CD
* Melhorias na documentação
* Refatorações e otimizações

---

# 👨‍💻 Autor

**Gabriel Eiji Sakuyama**

Estudante do Ensino Médio Integrado ao Técnico em Informática no Instituto Federal do Paraná (IFPR) - Campus Londrina.

Este projeto faz parte da minha jornada de estudos em desenvolvimento Backend com Java e Spring Boot.
