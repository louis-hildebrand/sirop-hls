.PHONY: run-fpga
run-fpga: fpga
	./camera

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./camera.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./camera.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./camera.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./camera.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: camera.prj

camera.prj: camera.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl --clock 175MHz ./camera.cpp -o camera

.PHONY: run-cpu
run-cpu: cpu
	./camera-cpu

.PHONY: cpu
cpu: camera-cpu

camera-cpu: camera.cpp
	i++ -march='x86-64' ./camera.cpp -o camera-cpu

.PHONY: clean
clean:
	rm -rf camera.prj/ camera camera-cpu transcript
