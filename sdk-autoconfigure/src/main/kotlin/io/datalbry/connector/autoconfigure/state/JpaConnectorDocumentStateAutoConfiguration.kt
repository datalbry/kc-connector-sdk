package io.datalbry.connector.autoconfigure.state

import io.datalbry.connector.sdk.state.jpa.DocumentRepository
import io.datalbry.connector.sdk.state.jpa.JobRepository
import io.datalbry.connector.sdk.state.jpa.JpaConnectorDocumentState
import io.datalbry.connector.sdk.state.jpa.LockRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@ConditionalOnClass(JpaConnectorDocumentState::class)
@EnableJpaRepositories
open class JpaConnectorDocumentStateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JpaConnectorDocumentState::class)
    open fun jpaConnectorDocumentState(
        jobRepository: JobRepository,
        documentRepository: DocumentRepository,
        lockRepository: LockRepository
    ): JpaConnectorDocumentState {
        return JpaConnectorDocumentState(
            jobRepository,
            documentRepository,
            lockRepository,
        )
    }

}
