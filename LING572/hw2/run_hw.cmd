executable = build_dt.sh
getenv     = true
notification = complete
arguments  = "./train.vectors.txt ./test.vectors.txt 20 0.1 model_file_20_01 sys_output_20_01"
output = acc_file_20_01
transfer_executable = false
request_memory = 3*1024
queue