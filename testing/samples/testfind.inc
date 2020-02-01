> init
<<<
D HASH "[a-f0-9]+"
+ text.txt rooney.txt
> add text.txt
<<<
> commit "Added rooney"
<<<
> find "Added rooney"
${HASH}
<<<*
> find "M"
Found no commit with that message.
<<<
+ text2.txt rooney.txt
> add text2.txt
<<<
> commit "Added rooney"
<<<
> find "Added rooney"
${HASH}
${HASH}
<<<*