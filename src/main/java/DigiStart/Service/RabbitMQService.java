package DigiStart.Service;

import DigiStart.Config.RabbitMQConfig;
import DigiStart.Model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitMQService {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Enviar mensagem quando usuário é criado
    public void sendUserCreatedMessage(User user) {
        Map<String, Object> message = new HashMap<>();
        message.put("eventType", "USER_CREATED");
        message.put("userId", user.getId());
        message.put("userEmail", user.getEmail());
        message.put("userType", user.getTipo());
        message.put("timestamp", System.currentTimeMillis());

        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                RabbitMQConfig.USER_ROUTING_KEY,
                message
            );
            log.info("Mensagem de usuário criado enviada para RabbitMQ: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem de usuário criado: {}", e.getMessage());
        }
    }

    // Enviar notificação para microserviço de conteúdo
    public void sendNotificationMessage(String message, String recipientEmail) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("recipientEmail", recipientEmail);
        notification.put("timestamp", System.currentTimeMillis());

        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                notification
            );
            log.info("Notificação enviada para: {}", recipientEmail);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação: {}", e.getMessage());
        }
    }

    // Enviar mensagem para microserviço de conteúdo
    public void sendContentSyncMessage(Long userId, String action) {
        Map<String, Object> message = new HashMap<>();
        message.put("eventType", "USER_SYNC");
        message.put("userId", userId);
        message.put("action", action);
        message.put("timestamp", System.currentTimeMillis());

        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONTENT_EXCHANGE,
                RabbitMQConfig.CONTENT_ROUTING_KEY,
                message
            );
            log.info("Mensagem de sincronização enviada para microserviço de conteúdo: userId={}, action={}", userId, action);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem de sincronização: {}", e.getMessage());
        }
    }

    // Enviar solicitação de criação de módulo com dados completos
    public void sendModuleCreationMessage(Long professorId, String nomeModulo, String descricaoModulo) {
        Map<String, Object> message = new HashMap<>();
        message.put("eventType", "CREATE_MODULE");
        message.put("professorId", professorId);
        message.put("nome", nomeModulo);
        message.put("descricao", descricaoModulo);
        message.put("ativo", true);
        message.put("timestamp", System.currentTimeMillis());

        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONTENT_EXCHANGE,
                "module.create",
                message
            );
            log.info("Solicitação de criação de módulo enviada: professorId={}, nome={}", professorId, nomeModulo);
        } catch (Exception e) {
            log.error("Erro ao enviar solicitação de criação de módulo: {}", e.getMessage());
        }
    }

    // Enviar solicitação para listar módulos de um professor
    public void sendListModulesRequest(Long professorId) {
        Map<String, Object> message = new HashMap<>();
        message.put("eventType", "LIST_MODULES");
        message.put("professorId", professorId);
        message.put("timestamp", System.currentTimeMillis());
        message.put("replyTo", "module.response." + professorId);

        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONTENT_EXCHANGE,
                "module.list",
                message
            );
            log.info("Solicitação de listagem de módulos enviada: professorId={}", professorId);
        } catch (Exception e) {
            log.error("Erro ao enviar solicitação de listagem de módulos: {}", e.getMessage());
        }
    }
}
