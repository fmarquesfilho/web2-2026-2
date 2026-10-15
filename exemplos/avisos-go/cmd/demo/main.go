// Comando demo: chama o Avisador direto, sem rede, para ver o domínio funcionando.
//
//	go run ./cmd/demo
package main

import (
	"context"
	"fmt"
	"time"

	"avisos/avisos"
)

func main() {
	av := avisos.NovoAvisador(avisos.CanaisPadrao()...)

	for _, prazo := range []time.Duration{time.Second, 100 * time.Millisecond} {
		ctx, cancelar := context.WithTimeout(context.Background(), prazo)
		inicio := time.Now()
		entregas, err := av.Avisar(ctx, avisos.Aviso{TarefaID: 1, Titulo: "Estudar Go"})
		cancelar()
		if err != nil {
			fmt.Println("erro:", err)
			continue
		}
		fmt.Printf("prazo %v, levou %v\n", prazo, time.Since(inicio).Round(10*time.Millisecond))
		for _, e := range entregas {
			if e.Erro != nil {
				fmt.Printf("  %-5s não entregue: %v\n", e.Canal, e.Erro)
			} else {
				fmt.Printf("  %-5s entregue\n", e.Canal)
			}
		}
	}

	_, err := av.Avisar(context.Background(), avisos.Aviso{TarefaID: 0, Titulo: ""})
	fmt.Println("aviso vazio:", err)
}
