install.packages("ggplot2")
library(ggplot2)
df <- read.csv(file.choose(), header=T, sep=",")
str(df)
cores = c("#47CCAF", "#C347CC")

# usando leitores e escritores
ggplot(df, aes(x=readers)) +
  geom_point(aes(y=df$time_rw, color="Leitores")) +
  geom_smooth(aes(y=df$time_rw), colour=cores[2]) +
  labs(x="leitores", y="tempo em milissegundos", title="Quantidade de leitores x tempo", 
       subtitle="usando tecnica dos leitores e escritores") +
  scale_color_manual(name="Legenda:", values=cores[2])

#Esse
ggplot(df, aes(x=writers)) +
  geom_point(aes(y=df$time_rw, color="Escritores")) +
  geom_smooth(aes(y=df$time_rw), colour=cores[1], se = 'F') +
  labs(x="escritores", y="tempo em milissegundos", title="Quantidade de escritores x tempo", 
       subtitle="usando tecnica dos leitores e escritores") +
  scale_color_manual(name="Legenda:", values=cores[1])

#Esse
# usando espera ocupada
ggplot(df, aes(x=readers)) +
  geom_point(aes(y=df$time_bw, color="Leitores")) +
  geom_smooth(aes(y=df$time_bw), colour=cores[2]) +
  labs(x="leitores", y="tempo em milissegundos", title="Quantidade de leitores x tempo", 
       subtitle="usando espera ocupada") +
  scale_color_manual(name="Legenda:", values=cores[2])

ggplot(df, aes(x=writers)) +
  geom_point(aes(y=df$time_bw, color="Escritores")) +
  geom_smooth(aes(y=df$time_bw), color=cores[1]) +
  labs(x="escritores", y="tempo em milissegundos", title="Quantidade de escritores x tempo", 
       subtitle="usando espera ocupada") +
  scale_color_manual(name="Legenda:", values=cores[1])
  
# dois juntos rw
ggplot(df, aes(y=time_rw)) +
  geom_point(aes(x=df$readers, color="Leitores")) +
  geom_smooth(aes(x=df$readers), color=cores[2], se=F) +
  geom_point(aes(x=df$writers, color="Escritores")) +
  geom_smooth(aes(x=df$writers), color=cores[1], se=F) +
  labs(x="leitores e escritores", y="tempo em milissegundos", title="Quantidade de leitores e escritores x tempo", 
       subtitle="usando tecnica dos leitores e escritores") +
  scale_color_manual(name="Legenda:", values=cores)

# dois juntos bw
ggplot(df, aes(y=time_bw)) +
  geom_point(aes(x=df$readers, color="Leitores")) +
  geom_smooth(aes(x=df$readers), color=cores[1], se=F) +
  geom_point(aes(x=df$writers, color="Escritores")) +
  geom_smooth(aes(x=df$writers), color=cores[2], se=F) +
  labs(x="leitores e escritores", y="tempo em milissegundos", title="Quantidade de leitores e escritores x tempo", 
       subtitle="usando espera ocupada") +
  scale_color_manual(name="Legenda:", values=cores)

