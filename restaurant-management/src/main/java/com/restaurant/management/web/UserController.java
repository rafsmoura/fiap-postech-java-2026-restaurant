package com.restaurant.management.web;

import com.restaurant.management.dto.PasswordChangeRequest;
import com.restaurant.management.dto.UserCreateRequest;
import com.restaurant.management.dto.UserResponse;
import com.restaurant.management.dto.UserUpdateRequest;
import com.restaurant.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "Localização sempre por query: id, email, name ou nameContains. Fluxo: GET /lookup → PUT /update, PUT /password ou DELETE (sem id no path). Lista: GET /users com id, name, email ou search.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Cadastrar usuário", description = "Cria dono de restaurante ou cliente. E-mail e login devem ser únicos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validação falhou (ProblemDetail)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
            @ApiResponse(responseCode = "409", description = "E-mail ou login duplicado",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "type": "https://techchallenge.restaurant/problems/email-duplicate",
                                      "title": "Conflito de e-mail",
                                      "status": 409,
                                      "detail": "E-mail já cadastrado: maria@email.com"
                                    }
                                    """)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @Operation(
            operationId = "lookupUserBeforeAction",
            summary = "1) Buscar um usuário (antes de atualizar / excluir / trocar senha)",
            description = """
                    **Único endpoint** para obter **um** usuário. Informe **exatamente um** parâmetro:
                    - **id** — número do cadastro
                    - **email** — e-mail completo (único)
                    - **name** — nome completo **exato** (ignora maiúsculas)
                    - **nameContains** — qualquer **trecho** do nome (ignora maiúsculas); se bater em mais de uma pessoa, a API pede que você use **email** ou **id**.
                    Não use `GET .../users/{id}` — essa forma foi removida para tudo passar por estes parâmetros."""
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nenhum ou mais de um parâmetro; vários resultados para o trecho",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    @GetMapping("/lookup")
    public UserResponse getByLookup(
            @Parameter(description = "Buscar pelo ID", example = "1")
            @RequestParam(required = false) Long id,
            @Parameter(description = "Buscar pelo e-mail", example = "maria.nova@email.com")
            @RequestParam(required = false) String email,
            @Parameter(description = "Nome completo exato", example = "Maria Silva Santos")
            @RequestParam(required = false) String name,
            @Parameter(description = "Trecho do nome (contém)", example = "Maria")
            @RequestParam(required = false) String nameContains) {
        return userService.findByLookup(id, email, name, nameContains);
    }

    @Operation(
            summary = "Listar / buscar usuários (barra de busca)",
            description = """
                    Retorna **lista**. Sem parâmetros, retorna todos.
                    Use **no máximo um** filtro:
                    - **id** — usuário com esse número (lista de 0 ou 1 item)
                    - **name** — trecho do **nome**
                    - **email** — trecho do **e-mail**
                    - **search** — mesmo texto no **nome ou no e-mail** (barra geral)
                    Para **um** registro com regras exatas/ambíguo, use **`GET /lookup`**.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista (pode ser vazia)"),
            @ApiResponse(responseCode = "400", description = "Mais de um filtro ao mesmo tempo",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    @GetMapping
    public List<UserResponse> search(
            @Parameter(description = "Filtrar por ID", example = "1")
            @RequestParam(required = false) Long id,
            @Parameter(description = "Trecho do nome", example = "Maria")
            @RequestParam(required = false) String name,
            @Parameter(description = "Trecho do e-mail", example = "gmail")
            @RequestParam(required = false) String email,
            @Parameter(description = "Busca em nome OU e-mail (contém)", example = "silva")
            @RequestParam(required = false) String search) {
        return userService.searchUsers(id, name, email, search);
    }

    @Operation(
            operationId = "changePasswordAfterLookup",
            summary = "Trocar senha (mesmos parâmetros do lookup + corpo)",
            description = """
                    Mesma localização que **`GET /lookup`**: **exatamente um** entre **id**, **email**, **name** ou **nameContains**.
                    Recomenda-se **`GET /lookup`** antes para confirmar o usuário. Depois envie senha atual e nova no corpo.
                    **Não** há senha por `{id}` na URL — use **id** (ou email, name, nameContains) na query abaixo."""
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha alterada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de busca inválidos, nome ambíguo ou senha atual incorreta",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    @PutMapping("/password")
    public UserResponse changePasswordByLookup(
            @Parameter(description = "ID do usuário", example = "1")
            @RequestParam(required = false) Long id,
            @Parameter(description = "E-mail do usuário", example = "maria@email.com")
            @RequestParam(required = false) String email,
            @Parameter(description = "Nome exato", example = "Maria Silva")
            @RequestParam(required = false) String name,
            @Parameter(description = "Trecho do nome", example = "Maria")
            @RequestParam(required = false) String nameContains,
            @Valid @RequestBody PasswordChangeRequest request) {
        return userService.changePasswordByLookup(id, email, name, nameContains, request);
    }

    @Operation(
            operationId = "updateUserAfterLookup",
            summary = "2) Atualizar dados (localizar por parâmetro + corpo JSON)",
            description = """
                    **Passo 1 (opcional mas recomendado):** use **`GET /api/v1/users/lookup`** com o **mesmo** `id`, `email`, `name` ou `nameContains` para ver o cadastro atual.
                    **Passo 2:** neste endpoint, preencha **exatamente um** dos parâmetros abaixo (igual ao lookup) e o JSON com os novos dados.
                    O servidor **busca** o usuário no banco e **só então** aplica a atualização (nome, e-mail, login, endereço). **Não** altera senha.
                    **Não** existe `PUT .../users/{id}` para perfil — o id vai só no parâmetro **id** ou você usa **email** / **name** / **nameContains**."""
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nenhum ou mais de um parâmetro de busca; nome ambíguo; validação do corpo",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
            @ApiResponse(responseCode = "409", description = "E-mail ou login já usado por outro usuário",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    @PutMapping("/update")
    public UserResponse updateProfile(
            @Parameter(description = "Buscar pelo ID numérico", example = "1")
            @RequestParam(required = false) Long id,
            @Parameter(description = "Buscar pelo e-mail cadastrado", example = "maria.nova@email.com")
            @RequestParam(required = false) String email,
            @Parameter(description = "Nome completo exato", example = "Maria Silva Santos")
            @RequestParam(required = false) String name,
            @Parameter(description = "Trecho do nome (único resultado)", example = "Maria")
            @RequestParam(required = false) String nameContains,
            @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateProfileByLookup(id, email, name, nameContains, request);
    }

    @Operation(
            summary = "Excluir usuário (localiza por id, e-mail ou nome, depois remove)",
            description = """
                    **Exatamente um** entre **id**, **email**, **name** ou **nameContains**, depois exclui (mesma regra do `GET /lookup`).
                    **Não** use id na URL — só na query abaixo."""
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removido"),
            @ApiResponse(responseCode = "400", description = "Nenhum ou mais de um parâmetro; nome ambíguo",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByLookup(
            @Parameter(description = "ID do usuário a excluir", example = "1")
            @RequestParam(required = false) Long id,
            @Parameter(description = "E-mail do usuário a excluir", example = "maria@email.com")
            @RequestParam(required = false) String email,
            @Parameter(description = "Nome exato", example = "Maria Silva")
            @RequestParam(required = false) String name,
            @Parameter(description = "Trecho do nome", example = "Maria")
            @RequestParam(required = false) String nameContains) {
        userService.deleteByLookup(id, email, name, nameContains);
    }

}
