# Compliance

Serviço Spring Boot para cadastrar empresas e iniciar uma consulta de risco de sanções após a criação. O projeto organiza o código em domínio, aplicação e infraestrutura, e integra com um serviço externo de sanções usando Spring Cloud OpenFeign.

> **Estado atual:** a consulta externa é disparada, mas o resultado ainda não é aplicado à política de compliance nem salvo como avaliação de risco. A política e os modelos AML existem no domínio, porém não estão conectados ao fluxo principal.

## Tecnologias

- Java 25
- Maven
- Spring Boot 4.1.1
- Spring Data REST e Spring Data KeyValue, com repositório em memória
- Spring Cloud OpenFeign
- Spring Cloud CircuitBreaker com Resilience4j
- Lombok

## Executar

Pré-requisitos: JDK 25 e Maven instalados, além do serviço externo de sanções acessível na URL configurada.

Na pasta `compliance`, execute:

```bash
mvn clean spring-boot:run
```

O `clean` remove classes compiladas antigas antes de iniciar a aplicação. O servidor web usa a porta padrão do Spring Boot, `8080`.

Para compilar sem iniciar o servidor:

```bash
mvn clean compile
```

## API de empresas

O Spring Data REST expõe o repositório `CompanyEntityRepository` em `/companies`. A aplicação não declara um controller próprio para esse recurso.

Cadastrar uma empresa:

```http
POST http://localhost:8080/companies
Content-Type: application/json
```

Exemplo de corpo:

```json
{
  "id": "4f519e53-8ca9-44df-89be-8cfd37ceef5a",
  "name": "Empresa Exemplo",
  "registrationNumber": "REG-1234",
  "riskAssessment": null
}
```

A entidade usa UUID como identificador. Após o cadastro, o evento `@HandleAfterCreate` de `CompanyEventHandler` chama `AnalyzeCompanyRiskUseCase` de forma síncrona. Portanto, uma falha na chamada externa pode aparecer durante o processamento do POST.

## Fluxo atual

1. O Spring Data REST recebe o cadastro em `/companies` e salva uma `CompanyEntity` no repositório de mapa em memória.
2. `CompanyEventHandler` recebe o evento de criação e converte a entidade para `Company`.
3. `AnalyzeCompanyRiskUseCase` chama o `SanctionClient`, passando o número de registro da empresa.
4. O cliente Feign faz `GET /sanctions/companies/{registrationNumber}` no serviço de sanções.
5. Se acionado, o fallback de `SanctionClient` devolve uma lista vazia de correspondências.

O uso atual termina na consulta: `AnalyzeCompanyRiskUseCase` não converte a resposta para o modelo de domínio, não chama `CompliancePolicy.evaluate` e não atualiza ou persiste uma avaliação.

## Integração com sanções

A configuração está em `src/main/resources/application.properties`:

```properties
spring.cloud.openfeign.client.config.sanction-client.url=http://192.168.64.1:3001
spring.cloud.openfeign.client.config.sanction-client.logger-level=FULL
spring.cloud.openfeign.client.config.sanction-client.default-request-headers.x-api-key=kyc-secret-123
spring.cloud.openfeign.circuitbreaker.enabled=true
```

O endereço deve ser alcançável a partir do processo que executa `compliance`. Se o serviço de sanções estiver em outra máquina ou container, ajuste a URL para um endereço e uma porta acessíveis nesse contexto. Um nome de serviço Docker só é resolvido entre containers conectados à mesma rede Docker.

A chave da API está atualmente escrita diretamente no arquivo de configuração; em ambientes reais, use configuração externa e não versione segredos. O nível `FULL` do Feign registra detalhes das requisições e respostas; avalie o risco de expor dados sensíveis nos logs.

O Circuit Breaker está habilitado e o cliente declara um fallback que retorna `SanctionResult` com `matches` vazia. Isso evita falhar a chamada Feign quando o fallback é acionado, mas não substitui a disponibilidade do serviço nem produz uma avaliação real de risco.

## Modelo de domínio

- `Company` representa a empresa por meio de `CompanyId`, nome, número de registro e uma avaliação opcional.
- `ComplianceScreening` agrupa correspondências de sanções e um perfil AML. O perfil inclui pontuação, indicadores, presença de PEP e exposições políticas.
- `RiskAssessment` contém pontuação, nível e status.
- `CompanyRepository` define o contrato de persistência; `InMemoryCompanyRepository` adapta esse contrato ao repositório Spring Data.

A regra em `CompliancePolicy.evaluate` determina o status nesta ordem:

- Qualquer correspondência de sanções com confiança maior que `0.8` resulta em `REJECTED`.
- Caso contrário, a presença de PEP resulta em `MANUAL_REVIEW`.
- Se ainda estiver `APPROVED`, uma pontuação AML maior que `70` muda o status para `MANUAL_REVIEW`.

O nível é calculado em `RiskAssessment`: status rejeitado resulta em `CRITICAL`; caso contrário, pontuação acima de `70` resulta em `HIGH`, acima de `30` em `MEDIUM`, e as demais em `LOW`.

Essas regras descrevem o comportamento da política quando chamada; no momento, o caso de uso não a chama e não existe cliente AML integrado.

## Organização do código

```text
src/main/java/org/compliance
├── Compliance.java                         # Inicialização e configuração de repositórios/Feign
├── application/                             # Casos de uso
├── domain/                                  # Entidades, política e tipos de domínio
└── infrastructure/
    ├── persistence/                          # Entidade, repositórios e evento pós-criação
    └── rest/                                 # Cliente Feign e DTO da resposta de sanções
```

## Observações

- A persistência é em memória; os dados não são mantidos após reiniciar a aplicação.
- Não há testes automatizados no diretório `src/test` neste momento.
- Não há endpoint próprio para executar uma avaliação completa de risco; o fluxo começa como efeito colateral da criação da empresa.
- A resposta do serviço de sanções e a política de compliance ainda precisam ser conectadas para que o cadastro resulte em uma avaliação persistida.
