install.packages("ggplot2")
library(ggplot2)
df <- read.csv(file.choose(), header=T, sep=",")
str(df)
cores = c("#47CCAF", "#C347CC")

#Numero de Escritores x Tempo (RW).
ggplot(df, aes(x = writers)) +
  geom_point(aes(y = df$time_rw, color = "Escritores")) +
  geom_smooth(aes(y = df$time_rw), colour = cores[2], se = 'F') +
  labs(x = "Escritores", y = "Tempo em Milissegundos", title = "Número de Escritores x Tempo", 
       subtitle = "Usando a solução Readers/Writers") +
  scale_color_manual(name = "Legenda:", values = cores[2])

#Numero de Escritores x Tempo (BW).
ggplot(df, aes(x = writers)) +
  geom_point(aes(y = df$time_bw, color = "Escritores")) +
  geom_smooth(aes(y = df$time_bw), color = cores[1], se = 'F') +
  labs(x = "Escritores", y = "Tempo em Milissegundos", title = "Número de Escritores x Tempo", 
       subtitle = "Usando a solução Espera Ocupada") +
  scale_color_manual(name = "Legenda:", values = cores[1])

#Cruzando os dois resultados.
ggplot(df, aes(x = writers)) +
  geom_point(aes(y = df$time_rw, color = "Readers/Writers"), se = 'F') +
  geom_point(aes(y = df$time_bw, color = "Espera Ocupada"), se = 'F') +
  labs(x = "Escritores", y = "Tempo em Milissegundos", title = "Número de Escritores x Tempo", 
       subtitle = "Cruzando os dois resultados") +
  scale_color_manual(name = "Legenda:", values = cores)


