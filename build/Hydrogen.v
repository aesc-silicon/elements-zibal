// Generator : SpinalHDL v1.4.0    git head : ecb5a80b713566f417ea3ea061f9969e73770a7f
// Date      : 05/06/2020, 14:12:10
// Component : Hydrogen


`define AluBitwiseCtrlEnum_defaultEncoding_type [1:0]
`define AluBitwiseCtrlEnum_defaultEncoding_XOR_1 2'b00
`define AluBitwiseCtrlEnum_defaultEncoding_OR_1 2'b01
`define AluBitwiseCtrlEnum_defaultEncoding_AND_1 2'b10

`define Src2CtrlEnum_defaultEncoding_type [1:0]
`define Src2CtrlEnum_defaultEncoding_RS 2'b00
`define Src2CtrlEnum_defaultEncoding_IMI 2'b01
`define Src2CtrlEnum_defaultEncoding_IMS 2'b10
`define Src2CtrlEnum_defaultEncoding_PC 2'b11

`define AluCtrlEnum_defaultEncoding_type [1:0]
`define AluCtrlEnum_defaultEncoding_ADD_SUB 2'b00
`define AluCtrlEnum_defaultEncoding_SLT_SLTU 2'b01
`define AluCtrlEnum_defaultEncoding_BITWISE 2'b10

`define EnvCtrlEnum_defaultEncoding_type [1:0]
`define EnvCtrlEnum_defaultEncoding_NONE 2'b00
`define EnvCtrlEnum_defaultEncoding_XRET 2'b01
`define EnvCtrlEnum_defaultEncoding_WFI 2'b10
`define EnvCtrlEnum_defaultEncoding_ECALL 2'b11

`define Src1CtrlEnum_defaultEncoding_type [1:0]
`define Src1CtrlEnum_defaultEncoding_RS 2'b00
`define Src1CtrlEnum_defaultEncoding_IMU 2'b01
`define Src1CtrlEnum_defaultEncoding_PC_INCREMENT 2'b10
`define Src1CtrlEnum_defaultEncoding_URS1 2'b11

`define BranchCtrlEnum_defaultEncoding_type [1:0]
`define BranchCtrlEnum_defaultEncoding_INC 2'b00
`define BranchCtrlEnum_defaultEncoding_B 2'b01
`define BranchCtrlEnum_defaultEncoding_JAL 2'b10
`define BranchCtrlEnum_defaultEncoding_JALR 2'b11

`define ShiftCtrlEnum_defaultEncoding_type [1:0]
`define ShiftCtrlEnum_defaultEncoding_DISABLE_1 2'b00
`define ShiftCtrlEnum_defaultEncoding_SLL_1 2'b01
`define ShiftCtrlEnum_defaultEncoding_SRL_1 2'b10
`define ShiftCtrlEnum_defaultEncoding_SRA_1 2'b11

`define JtagState_defaultEncoding_type [3:0]
`define JtagState_defaultEncoding_RESET 4'b0000
`define JtagState_defaultEncoding_IDLE 4'b0001
`define JtagState_defaultEncoding_IR_SELECT 4'b0010
`define JtagState_defaultEncoding_IR_CAPTURE 4'b0011
`define JtagState_defaultEncoding_IR_SHIFT 4'b0100
`define JtagState_defaultEncoding_IR_EXIT1 4'b0101
`define JtagState_defaultEncoding_IR_PAUSE 4'b0110
`define JtagState_defaultEncoding_IR_EXIT2 4'b0111
`define JtagState_defaultEncoding_IR_UPDATE 4'b1000
`define JtagState_defaultEncoding_DR_SELECT 4'b1001
`define JtagState_defaultEncoding_DR_CAPTURE 4'b1010
`define JtagState_defaultEncoding_DR_SHIFT 4'b1011
`define JtagState_defaultEncoding_DR_EXIT1 4'b1100
`define JtagState_defaultEncoding_DR_PAUSE 4'b1101
`define JtagState_defaultEncoding_DR_EXIT2 4'b1110
`define JtagState_defaultEncoding_DR_UPDATE 4'b1111

`define Axi4ToApb3BridgePhase_defaultEncoding_type [1:0]
`define Axi4ToApb3BridgePhase_defaultEncoding_SETUP 2'b00
`define Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 2'b01
`define Axi4ToApb3BridgePhase_defaultEncoding_RESPONSE 2'b10

`define ParityType_defaultEncoding_type [1:0]
`define ParityType_defaultEncoding_NONE 2'b00
`define ParityType_defaultEncoding_EVEN 2'b01
`define ParityType_defaultEncoding_ODD 2'b10

`define StopType_defaultEncoding_type [0:0]
`define StopType_defaultEncoding_ONE 1'b0
`define StopType_defaultEncoding_TWO 1'b1

`define State_defaultEncoding_type [2:0]
`define State_defaultEncoding_IDLE 3'b000
`define State_defaultEncoding_START 3'b001
`define State_defaultEncoding_DATA 3'b010
`define State_defaultEncoding_PARITY 3'b011
`define State_defaultEncoding_STOP 3'b100

`define State_1_defaultEncoding_type [2:0]
`define State_1_defaultEncoding_IDLE 3'b000
`define State_1_defaultEncoding_START 3'b001
`define State_1_defaultEncoding_DATA 3'b010
`define State_1_defaultEncoding_PARITY 3'b011
`define State_1_defaultEncoding_STOP 3'b100

`define CmdMode_defaultEncoding_type [0:0]
`define CmdMode_defaultEncoding_DATA 1'b0
`define CmdMode_defaultEncoding_SS 1'b1


module BufferCC (
  input               io_initial,
  input               io_dataIn,
  output              io_dataOut,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg                 buffers_0;
  reg                 buffers_1;

  assign io_dataOut = buffers_1;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      buffers_0 <= io_initial;
      buffers_1 <= io_initial;
    end else begin
      buffers_0 <= io_dataIn;
      buffers_1 <= buffers_0;
    end
  end


endmodule
//BufferCC_1_ replaced by BufferCC
//BufferCC_2_ replaced by BufferCC

module BufferCC_3_ (
  input               io_dataIn,
  output              io_dataOut,
  input               io_clock,
  input               resetCtrl_debugReset 
);
  reg                 buffers_0;
  reg                 buffers_1;

  assign io_dataOut = buffers_1;
  always @ (posedge io_clock) begin
    buffers_0 <= io_dataIn;
    buffers_1 <= buffers_0;
  end


endmodule

module UartCtrlTx (
  input      `ParityType_defaultEncoding_type io_config_parity,
  input      `StopType_defaultEncoding_type io_config_stop,
  input      [3:0]    io_config_dataLength,
  input               io_samplingTick,
  input               io_write_valid,
  output reg          io_write_ready,
  input      [8:0]    io_write_payload,
  output              io_txd,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_1_;
  wire       [0:0]    _zz_2_;
  wire       [2:0]    _zz_3_;
  wire       [0:0]    _zz_4_;
  wire       [3:0]    _zz_5_;
  reg                 txEnable;
  wire                txCtrl_newClockEnable;
  reg                 txCtrl_clockDivider_counter_willIncrement;
  wire                txCtrl_clockDivider_counter_willClear;
  reg        [2:0]    txCtrl_clockDivider_counter_valueNext;
  reg        [2:0]    txCtrl_clockDivider_counter_value;
  wire                txCtrl_clockDivider_counter_willOverflowIfInc;
  wire                txCtrl_clockDivider_counter_willOverflow;
  reg        [3:0]    txCtrl_tickCounter_value;
  reg        `State_defaultEncoding_type txCtrl_stateMachine_state;
  reg                 txCtrl_stateMachine_parity;
  reg                 txCtrl_stateMachine_txd;
  reg                 txCtrl_stateMachine_txd_regNext;
  `ifndef SYNTHESIS
  reg [31:0] io_config_parity_string;
  reg [23:0] io_config_stop_string;
  reg [47:0] txCtrl_stateMachine_state_string;
  `endif


  assign _zz_1_ = (txCtrl_tickCounter_value == io_config_dataLength);
  assign _zz_2_ = txCtrl_clockDivider_counter_willIncrement;
  assign _zz_3_ = {2'd0, _zz_2_};
  assign _zz_4_ = ((io_config_stop == `StopType_defaultEncoding_ONE) ? (1'b0) : (1'b1));
  assign _zz_5_ = {3'd0, _zz_4_};
  `ifndef SYNTHESIS
  always @(*) begin
    case(io_config_parity)
      `ParityType_defaultEncoding_NONE : io_config_parity_string = "NONE";
      `ParityType_defaultEncoding_EVEN : io_config_parity_string = "EVEN";
      `ParityType_defaultEncoding_ODD : io_config_parity_string = "ODD ";
      default : io_config_parity_string = "????";
    endcase
  end
  always @(*) begin
    case(io_config_stop)
      `StopType_defaultEncoding_ONE : io_config_stop_string = "ONE";
      `StopType_defaultEncoding_TWO : io_config_stop_string = "TWO";
      default : io_config_stop_string = "???";
    endcase
  end
  always @(*) begin
    case(txCtrl_stateMachine_state)
      `State_defaultEncoding_IDLE : txCtrl_stateMachine_state_string = "IDLE  ";
      `State_defaultEncoding_START : txCtrl_stateMachine_state_string = "START ";
      `State_defaultEncoding_DATA : txCtrl_stateMachine_state_string = "DATA  ";
      `State_defaultEncoding_PARITY : txCtrl_stateMachine_state_string = "PARITY";
      `State_defaultEncoding_STOP : txCtrl_stateMachine_state_string = "STOP  ";
      default : txCtrl_stateMachine_state_string = "??????";
    endcase
  end
  `endif

  assign txCtrl_newClockEnable = (1'b1 && txEnable);
  always @ (*) begin
    txCtrl_clockDivider_counter_willIncrement = 1'b0;
    if(io_samplingTick)begin
      txCtrl_clockDivider_counter_willIncrement = 1'b1;
    end
  end

  assign txCtrl_clockDivider_counter_willClear = 1'b0;
  assign txCtrl_clockDivider_counter_willOverflowIfInc = (txCtrl_clockDivider_counter_value == (3'b111));
  assign txCtrl_clockDivider_counter_willOverflow = (txCtrl_clockDivider_counter_willOverflowIfInc && txCtrl_clockDivider_counter_willIncrement);
  always @ (*) begin
    txCtrl_clockDivider_counter_valueNext = (txCtrl_clockDivider_counter_value + _zz_3_);
    if(txCtrl_clockDivider_counter_willClear)begin
      txCtrl_clockDivider_counter_valueNext = (3'b000);
    end
  end

  always @ (*) begin
    txCtrl_stateMachine_txd = 1'b1;
    case(txCtrl_stateMachine_state)
      `State_defaultEncoding_IDLE : begin
      end
      `State_defaultEncoding_START : begin
        txCtrl_stateMachine_txd = 1'b0;
      end
      `State_defaultEncoding_DATA : begin
        txCtrl_stateMachine_txd = io_write_payload[txCtrl_tickCounter_value];
      end
      `State_defaultEncoding_PARITY : begin
        txCtrl_stateMachine_txd = txCtrl_stateMachine_parity;
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    io_write_ready = 1'b0;
    case(txCtrl_stateMachine_state)
      `State_defaultEncoding_IDLE : begin
      end
      `State_defaultEncoding_START : begin
      end
      `State_defaultEncoding_DATA : begin
        if(txCtrl_clockDivider_counter_willOverflow)begin
          if(_zz_1_)begin
            io_write_ready = 1'b1;
          end
        end
      end
      `State_defaultEncoding_PARITY : begin
      end
      default : begin
      end
    endcase
  end

  assign io_txd = txCtrl_stateMachine_txd_regNext;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      txEnable <= 1'b1;
      txCtrl_stateMachine_txd_regNext <= 1'b1;
    end else begin
      if((io_write_valid || (! (txCtrl_stateMachine_state == `State_defaultEncoding_IDLE))))begin
        txEnable <= 1'b1;
      end else begin
        txEnable <= 1'b0;
      end
      txCtrl_stateMachine_txd_regNext <= txCtrl_stateMachine_txd;
    end
  end

  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      txCtrl_clockDivider_counter_value <= (3'b000);
      txCtrl_stateMachine_state <= `State_defaultEncoding_IDLE;
    end else begin
      if(txCtrl_newClockEnable) begin
        txCtrl_clockDivider_counter_value <= txCtrl_clockDivider_counter_valueNext;
        case(txCtrl_stateMachine_state)
          `State_defaultEncoding_IDLE : begin
            if((io_write_valid && txCtrl_clockDivider_counter_willOverflow))begin
              txCtrl_stateMachine_state <= `State_defaultEncoding_START;
            end
          end
          `State_defaultEncoding_START : begin
            if(txCtrl_clockDivider_counter_willOverflow)begin
              txCtrl_stateMachine_state <= `State_defaultEncoding_DATA;
            end
          end
          `State_defaultEncoding_DATA : begin
            if(txCtrl_clockDivider_counter_willOverflow)begin
              if(_zz_1_)begin
                if((io_config_parity == `ParityType_defaultEncoding_NONE))begin
                  txCtrl_stateMachine_state <= `State_defaultEncoding_STOP;
                end else begin
                  txCtrl_stateMachine_state <= `State_defaultEncoding_PARITY;
                end
              end
            end
          end
          `State_defaultEncoding_PARITY : begin
            if(txCtrl_clockDivider_counter_willOverflow)begin
              txCtrl_stateMachine_state <= `State_defaultEncoding_STOP;
            end
          end
          default : begin
            if(txCtrl_clockDivider_counter_willOverflow)begin
              if((txCtrl_tickCounter_value == _zz_5_))begin
                txCtrl_stateMachine_state <= (io_write_valid ? `State_defaultEncoding_START : `State_defaultEncoding_IDLE);
              end
            end
          end
        endcase
      end
    end
  end

  always @ (posedge io_clock) begin
    if(txCtrl_newClockEnable) begin
      if(txCtrl_clockDivider_counter_willOverflow)begin
        txCtrl_tickCounter_value <= (txCtrl_tickCounter_value + (4'b0001));
      end
      if(txCtrl_clockDivider_counter_willOverflow)begin
        txCtrl_stateMachine_parity <= (txCtrl_stateMachine_parity ^ txCtrl_stateMachine_txd);
      end
      case(txCtrl_stateMachine_state)
        `State_defaultEncoding_IDLE : begin
        end
        `State_defaultEncoding_START : begin
          if(txCtrl_clockDivider_counter_willOverflow)begin
            txCtrl_stateMachine_parity <= (io_config_parity == `ParityType_defaultEncoding_ODD);
            txCtrl_tickCounter_value <= (4'b0000);
          end
        end
        `State_defaultEncoding_DATA : begin
          if(txCtrl_clockDivider_counter_willOverflow)begin
            if(_zz_1_)begin
              txCtrl_tickCounter_value <= (4'b0000);
            end
          end
        end
        `State_defaultEncoding_PARITY : begin
          if(txCtrl_clockDivider_counter_willOverflow)begin
            txCtrl_tickCounter_value <= (4'b0000);
          end
        end
        default : begin
        end
      endcase
    end
  end


endmodule

module UartCtrlRx (
  input      `ParityType_defaultEncoding_type io_config_parity,
  input      `StopType_defaultEncoding_type io_config_stop,
  input      [3:0]    io_config_dataLength,
  input               io_samplingTick,
  output              io_read_valid,
  input               io_read_ready,
  output     [8:0]    io_read_payload,
  input               io_rxd,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_1_;
  wire                io_rxd_buffercc_io_dataOut;
  wire                _zz_2_;
  wire                _zz_3_;
  wire       [0:0]    _zz_4_;
  wire       [3:0]    _zz_5_;
  wire                _zz_6_;
  wire                _zz_7_;
  wire                _zz_8_;
  wire                _zz_9_;
  wire                _zz_10_;
  wire                _zz_11_;
  wire                _zz_12_;
  wire                sampler_synchroniser;
  wire                sampler_samples_0;
  reg                 sampler_samples_1;
  reg                 sampler_samples_2;
  reg                 sampler_samples_3;
  reg                 sampler_samples_4;
  reg                 sampler_value;
  reg                 sampler_tick;
  reg        [2:0]    bitTimer_counter;
  reg                 bitTimer_tick;
  reg        [3:0]    bitCounter_value;
  reg        `State_1_defaultEncoding_type stateMachine_state;
  reg                 stateMachine_parity;
  reg        [8:0]    stateMachine_shifter;
  reg                 stateMachine_validReg;
  `ifndef SYNTHESIS
  reg [31:0] io_config_parity_string;
  reg [23:0] io_config_stop_string;
  reg [47:0] stateMachine_state_string;
  `endif


  assign _zz_2_ = (sampler_tick && (! sampler_value));
  assign _zz_3_ = (bitCounter_value == io_config_dataLength);
  assign _zz_4_ = ((io_config_stop == `StopType_defaultEncoding_ONE) ? (1'b0) : (1'b1));
  assign _zz_5_ = {3'd0, _zz_4_};
  assign _zz_6_ = ((((1'b0 || ((_zz_11_ && sampler_samples_1) && sampler_samples_2)) || (((_zz_12_ && sampler_samples_0) && sampler_samples_1) && sampler_samples_3)) || (((1'b1 && sampler_samples_0) && sampler_samples_2) && sampler_samples_3)) || (((1'b1 && sampler_samples_1) && sampler_samples_2) && sampler_samples_3));
  assign _zz_7_ = (((1'b1 && sampler_samples_0) && sampler_samples_1) && sampler_samples_4);
  assign _zz_8_ = ((1'b1 && sampler_samples_0) && sampler_samples_2);
  assign _zz_9_ = (1'b1 && sampler_samples_1);
  assign _zz_10_ = 1'b1;
  assign _zz_11_ = (1'b1 && sampler_samples_0);
  assign _zz_12_ = 1'b1;
  BufferCC io_rxd_buffercc ( 
    .io_initial               (_zz_1_                      ), //i
    .io_dataIn                (io_rxd                      ), //i
    .io_dataOut               (io_rxd_buffercc_io_dataOut  ), //o
    .io_clock                 (io_clock                    ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset       )  //i
  );
  `ifndef SYNTHESIS
  always @(*) begin
    case(io_config_parity)
      `ParityType_defaultEncoding_NONE : io_config_parity_string = "NONE";
      `ParityType_defaultEncoding_EVEN : io_config_parity_string = "EVEN";
      `ParityType_defaultEncoding_ODD : io_config_parity_string = "ODD ";
      default : io_config_parity_string = "????";
    endcase
  end
  always @(*) begin
    case(io_config_stop)
      `StopType_defaultEncoding_ONE : io_config_stop_string = "ONE";
      `StopType_defaultEncoding_TWO : io_config_stop_string = "TWO";
      default : io_config_stop_string = "???";
    endcase
  end
  always @(*) begin
    case(stateMachine_state)
      `State_1_defaultEncoding_IDLE : stateMachine_state_string = "IDLE  ";
      `State_1_defaultEncoding_START : stateMachine_state_string = "START ";
      `State_1_defaultEncoding_DATA : stateMachine_state_string = "DATA  ";
      `State_1_defaultEncoding_PARITY : stateMachine_state_string = "PARITY";
      `State_1_defaultEncoding_STOP : stateMachine_state_string = "STOP  ";
      default : stateMachine_state_string = "??????";
    endcase
  end
  `endif

  assign _zz_1_ = 1'b0;
  assign sampler_synchroniser = io_rxd_buffercc_io_dataOut;
  assign sampler_samples_0 = sampler_synchroniser;
  always @ (*) begin
    bitTimer_tick = 1'b0;
    if(sampler_tick)begin
      if((bitTimer_counter == (3'b000)))begin
        bitTimer_tick = 1'b1;
      end
    end
  end

  assign io_read_valid = stateMachine_validReg;
  assign io_read_payload = stateMachine_shifter;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      sampler_samples_1 <= 1'b1;
      sampler_samples_2 <= 1'b1;
      sampler_samples_3 <= 1'b1;
      sampler_samples_4 <= 1'b1;
      sampler_value <= 1'b1;
      sampler_tick <= 1'b0;
      stateMachine_state <= `State_1_defaultEncoding_IDLE;
      stateMachine_validReg <= 1'b0;
    end else begin
      if(io_samplingTick)begin
        sampler_samples_1 <= sampler_samples_0;
      end
      if(io_samplingTick)begin
        sampler_samples_2 <= sampler_samples_1;
      end
      if(io_samplingTick)begin
        sampler_samples_3 <= sampler_samples_2;
      end
      if(io_samplingTick)begin
        sampler_samples_4 <= sampler_samples_3;
      end
      sampler_value <= ((((((_zz_6_ || _zz_7_) || (_zz_8_ && sampler_samples_4)) || ((_zz_9_ && sampler_samples_2) && sampler_samples_4)) || (((_zz_10_ && sampler_samples_0) && sampler_samples_3) && sampler_samples_4)) || (((1'b1 && sampler_samples_1) && sampler_samples_3) && sampler_samples_4)) || (((1'b1 && sampler_samples_2) && sampler_samples_3) && sampler_samples_4));
      sampler_tick <= io_samplingTick;
      stateMachine_validReg <= 1'b0;
      case(stateMachine_state)
        `State_1_defaultEncoding_IDLE : begin
          if(_zz_2_)begin
            stateMachine_state <= `State_1_defaultEncoding_START;
          end
        end
        `State_1_defaultEncoding_START : begin
          if(bitTimer_tick)begin
            stateMachine_state <= `State_1_defaultEncoding_DATA;
            if((sampler_value == 1'b1))begin
              stateMachine_state <= `State_1_defaultEncoding_IDLE;
            end
          end
        end
        `State_1_defaultEncoding_DATA : begin
          if(bitTimer_tick)begin
            if(_zz_3_)begin
              if((io_config_parity == `ParityType_defaultEncoding_NONE))begin
                stateMachine_state <= `State_1_defaultEncoding_STOP;
                stateMachine_validReg <= 1'b1;
              end else begin
                stateMachine_state <= `State_1_defaultEncoding_PARITY;
              end
            end
          end
        end
        `State_1_defaultEncoding_PARITY : begin
          if(bitTimer_tick)begin
            if((stateMachine_parity == sampler_value))begin
              stateMachine_state <= `State_1_defaultEncoding_STOP;
              stateMachine_validReg <= 1'b1;
            end else begin
              stateMachine_state <= `State_1_defaultEncoding_IDLE;
            end
          end
        end
        default : begin
          if(bitTimer_tick)begin
            if((! sampler_value))begin
              stateMachine_state <= `State_1_defaultEncoding_IDLE;
            end else begin
              if((bitCounter_value == _zz_5_))begin
                stateMachine_state <= `State_1_defaultEncoding_IDLE;
              end
            end
          end
        end
      endcase
    end
  end

  always @ (posedge io_clock) begin
    if(sampler_tick)begin
      bitTimer_counter <= (bitTimer_counter - (3'b001));
    end
    if(bitTimer_tick)begin
      bitCounter_value <= (bitCounter_value + (4'b0001));
    end
    if(bitTimer_tick)begin
      stateMachine_parity <= (stateMachine_parity ^ sampler_value);
    end
    case(stateMachine_state)
      `State_1_defaultEncoding_IDLE : begin
        if(_zz_2_)begin
          bitTimer_counter <= (3'b010);
        end
      end
      `State_1_defaultEncoding_START : begin
        if(bitTimer_tick)begin
          bitCounter_value <= (4'b0000);
          stateMachine_parity <= (io_config_parity == `ParityType_defaultEncoding_ODD);
          stateMachine_shifter <= 9'h0;
        end
      end
      `State_1_defaultEncoding_DATA : begin
        if(bitTimer_tick)begin
          stateMachine_shifter[bitCounter_value] <= sampler_value;
          if(_zz_3_)begin
            bitCounter_value <= (4'b0000);
          end
        end
      end
      `State_1_defaultEncoding_PARITY : begin
        if(bitTimer_tick)begin
          bitCounter_value <= (4'b0000);
        end
      end
      default : begin
      end
    endcase
  end


endmodule
//UartCtrlTx_1_ replaced by UartCtrlTx
//UartCtrlRx_1_ replaced by UartCtrlRx
//UartCtrlTx_2_ replaced by UartCtrlTx
//UartCtrlRx_2_ replaced by UartCtrlRx

module BufferCC_4_ (
  input      [2:0]    io_dataIn,
  output     [2:0]    io_dataOut,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [2:0]    buffers_0;
  reg        [2:0]    buffers_1;

  assign io_dataOut = buffers_1;
  always @ (posedge io_clock) begin
    buffers_0 <= io_dataIn;
    buffers_1 <= buffers_0;
  end


endmodule
//BufferCC_5_ replaced by BufferCC_4_

module BufferCC_6_ (
  input      [11:0]   io_dataIn,
  output     [11:0]   io_dataOut,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [11:0]   buffers_0;
  reg        [11:0]   buffers_1;

  assign io_dataOut = buffers_1;
  always @ (posedge io_clock) begin
    buffers_0 <= io_dataIn;
    buffers_1 <= buffers_0;
  end


endmodule
//BufferCC_7_ replaced by BufferCC_6_

module StreamFifoLowLatency (
  input               io_push_valid,
  output              io_push_ready,
  input               io_push_payload_error,
  input      [31:0]   io_push_payload_inst,
  output reg          io_pop_valid,
  input               io_pop_ready,
  output reg          io_pop_payload_error,
  output reg [31:0]   io_pop_payload_inst,
  input               io_flush,
  output     [1:0]    io_occupancy,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire       [32:0]   _zz_3_;
  wire                _zz_4_;
  wire       [0:0]    _zz_5_;
  wire       [32:0]   _zz_6_;
  reg                 _zz_1_;
  reg                 pushPtr_willIncrement;
  reg                 pushPtr_willClear;
  reg        [0:0]    pushPtr_valueNext;
  reg        [0:0]    pushPtr_value;
  wire                pushPtr_willOverflowIfInc;
  wire                pushPtr_willOverflow;
  reg                 popPtr_willIncrement;
  reg                 popPtr_willClear;
  reg        [0:0]    popPtr_valueNext;
  reg        [0:0]    popPtr_value;
  wire                popPtr_willOverflowIfInc;
  wire                popPtr_willOverflow;
  wire                ptrMatch;
  reg                 risingOccupancy;
  wire                empty;
  wire                full;
  wire                pushing;
  wire                popping;
  wire       [32:0]   _zz_2_;
  wire       [0:0]    ptrDif;
  reg [32:0] ram [0:1];

  assign _zz_4_ = (! empty);
  assign _zz_5_ = _zz_2_[0 : 0];
  assign _zz_6_ = {io_push_payload_inst,io_push_payload_error};
  assign _zz_3_ = ram[popPtr_value];
  always @ (posedge io_clock) begin
    if(_zz_1_) begin
      ram[pushPtr_value] <= _zz_6_;
    end
  end

  always @ (*) begin
    _zz_1_ = 1'b0;
    if(pushing)begin
      _zz_1_ = 1'b1;
    end
  end

  always @ (*) begin
    pushPtr_willIncrement = 1'b0;
    if(pushing)begin
      pushPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    pushPtr_willClear = 1'b0;
    if(io_flush)begin
      pushPtr_willClear = 1'b1;
    end
  end

  assign pushPtr_willOverflowIfInc = (pushPtr_value == (1'b1));
  assign pushPtr_willOverflow = (pushPtr_willOverflowIfInc && pushPtr_willIncrement);
  always @ (*) begin
    pushPtr_valueNext = (pushPtr_value + pushPtr_willIncrement);
    if(pushPtr_willClear)begin
      pushPtr_valueNext = (1'b0);
    end
  end

  always @ (*) begin
    popPtr_willIncrement = 1'b0;
    if(popping)begin
      popPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    popPtr_willClear = 1'b0;
    if(io_flush)begin
      popPtr_willClear = 1'b1;
    end
  end

  assign popPtr_willOverflowIfInc = (popPtr_value == (1'b1));
  assign popPtr_willOverflow = (popPtr_willOverflowIfInc && popPtr_willIncrement);
  always @ (*) begin
    popPtr_valueNext = (popPtr_value + popPtr_willIncrement);
    if(popPtr_willClear)begin
      popPtr_valueNext = (1'b0);
    end
  end

  assign ptrMatch = (pushPtr_value == popPtr_value);
  assign empty = (ptrMatch && (! risingOccupancy));
  assign full = (ptrMatch && risingOccupancy);
  assign pushing = (io_push_valid && io_push_ready);
  assign popping = (io_pop_valid && io_pop_ready);
  assign io_push_ready = (! full);
  always @ (*) begin
    if(_zz_4_)begin
      io_pop_valid = 1'b1;
    end else begin
      io_pop_valid = io_push_valid;
    end
  end

  assign _zz_2_ = _zz_3_;
  always @ (*) begin
    if(_zz_4_)begin
      io_pop_payload_error = _zz_5_[0];
    end else begin
      io_pop_payload_error = io_push_payload_error;
    end
  end

  always @ (*) begin
    if(_zz_4_)begin
      io_pop_payload_inst = _zz_2_[32 : 1];
    end else begin
      io_pop_payload_inst = io_push_payload_inst;
    end
  end

  assign ptrDif = (pushPtr_value - popPtr_value);
  assign io_occupancy = {(risingOccupancy && ptrMatch),ptrDif};
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      pushPtr_value <= (1'b0);
      popPtr_value <= (1'b0);
      risingOccupancy <= 1'b0;
    end else begin
      pushPtr_value <= pushPtr_valueNext;
      popPtr_value <= popPtr_valueNext;
      if((pushing != popping))begin
        risingOccupancy <= pushing;
      end
      if(io_flush)begin
        risingOccupancy <= 1'b0;
      end
    end
  end


endmodule

module FlowCCByToggle (
  input               io_input_valid,
  input               io_input_payload_last,
  input      [0:0]    io_input_payload_fragment,
  output              io_output_valid,
  output              io_output_payload_last,
  output     [0:0]    io_output_payload_fragment,
  input               io_jtag_tck,
  input               io_clock,
  input               resetCtrl_debugReset 
);
  wire                inputArea_target_buffercc_io_dataOut;
  wire                outHitSignal;
  reg                 inputArea_target = 0;
  reg                 inputArea_data_last;
  reg        [0:0]    inputArea_data_fragment;
  wire                outputArea_target;
  reg                 outputArea_hit;
  wire                outputArea_flow_valid;
  wire                outputArea_flow_payload_last;
  wire       [0:0]    outputArea_flow_payload_fragment;
  reg                 outputArea_flow_regNext_valid;
  reg                 outputArea_flow_regNext_payload_last;
  reg        [0:0]    outputArea_flow_regNext_payload_fragment;

  BufferCC_3_ inputArea_target_buffercc ( 
    .io_dataIn               (inputArea_target                      ), //i
    .io_dataOut              (inputArea_target_buffercc_io_dataOut  ), //o
    .io_clock                (io_clock                              ), //i
    .resetCtrl_debugReset    (resetCtrl_debugReset                  )  //i
  );
  assign outputArea_target = inputArea_target_buffercc_io_dataOut;
  assign outputArea_flow_valid = (outputArea_target != outputArea_hit);
  assign outputArea_flow_payload_last = inputArea_data_last;
  assign outputArea_flow_payload_fragment = inputArea_data_fragment;
  assign io_output_valid = outputArea_flow_regNext_valid;
  assign io_output_payload_last = outputArea_flow_regNext_payload_last;
  assign io_output_payload_fragment = outputArea_flow_regNext_payload_fragment;
  always @ (posedge io_jtag_tck) begin
    if(io_input_valid)begin
      inputArea_target <= (! inputArea_target);
      inputArea_data_last <= io_input_payload_last;
      inputArea_data_fragment <= io_input_payload_fragment;
    end
  end

  always @ (posedge io_clock) begin
    outputArea_hit <= outputArea_target;
    outputArea_flow_regNext_payload_last <= outputArea_flow_payload_last;
    outputArea_flow_regNext_payload_fragment <= outputArea_flow_payload_fragment;
  end

  always @ (posedge io_clock or posedge resetCtrl_debugReset) begin
    if (resetCtrl_debugReset) begin
      outputArea_flow_regNext_valid <= 1'b0;
    end else begin
      outputArea_flow_regNext_valid <= outputArea_flow_valid;
    end
  end


endmodule

module Axi4ReadOnlyErrorSlave (
  input               io_axi_ar_valid,
  output              io_axi_ar_ready,
  input      [31:0]   io_axi_ar_payload_addr,
  input      [3:0]    io_axi_ar_payload_cache,
  input      [2:0]    io_axi_ar_payload_prot,
  output              io_axi_r_valid,
  input               io_axi_r_ready,
  output     [31:0]   io_axi_r_payload_data,
  output     [1:0]    io_axi_r_payload_resp,
  output              io_axi_r_payload_last,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_1_;
  reg                 sendRsp;
  reg        [7:0]    remaining;
  wire                remainingZero;

  assign _zz_1_ = (io_axi_ar_valid && io_axi_ar_ready);
  assign remainingZero = (remaining == 8'h0);
  assign io_axi_ar_ready = (! sendRsp);
  assign io_axi_r_valid = sendRsp;
  assign io_axi_r_payload_resp = (2'b11);
  assign io_axi_r_payload_last = remainingZero;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      sendRsp <= 1'b0;
    end else begin
      if(_zz_1_)begin
        sendRsp <= 1'b1;
      end
      if(sendRsp)begin
        if(io_axi_r_ready)begin
          if(remainingZero)begin
            sendRsp <= 1'b0;
          end
        end
      end
    end
  end

  always @ (posedge io_clock) begin
    if(_zz_1_)begin
      remaining <= 8'h0;
    end
    if(sendRsp)begin
      if(io_axi_r_ready)begin
        remaining <= (remaining - 8'h01);
      end
    end
  end


endmodule

module Axi4SharedErrorSlave (
  input               io_axi_arw_valid,
  output              io_axi_arw_ready,
  input      [31:0]   io_axi_arw_payload_addr,
  input      [2:0]    io_axi_arw_payload_size,
  input      [3:0]    io_axi_arw_payload_cache,
  input      [2:0]    io_axi_arw_payload_prot,
  input               io_axi_arw_payload_write,
  input               io_axi_w_valid,
  output              io_axi_w_ready,
  input      [31:0]   io_axi_w_payload_data,
  input      [3:0]    io_axi_w_payload_strb,
  input               io_axi_w_payload_last,
  output              io_axi_b_valid,
  input               io_axi_b_ready,
  output     [1:0]    io_axi_b_payload_resp,
  output              io_axi_r_valid,
  input               io_axi_r_ready,
  output     [31:0]   io_axi_r_payload_data,
  output     [1:0]    io_axi_r_payload_resp,
  output              io_axi_r_payload_last,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_1_;
  reg                 consumeData;
  reg                 sendReadRsp;
  reg                 sendWriteRsp;
  reg        [7:0]    remaining;
  wire                remainingZero;

  assign _zz_1_ = (io_axi_arw_valid && io_axi_arw_ready);
  assign remainingZero = (remaining == 8'h0);
  assign io_axi_arw_ready = (! ((consumeData || sendWriteRsp) || sendReadRsp));
  assign io_axi_w_ready = consumeData;
  assign io_axi_b_valid = sendWriteRsp;
  assign io_axi_b_payload_resp = (2'b11);
  assign io_axi_r_valid = sendReadRsp;
  assign io_axi_r_payload_resp = (2'b11);
  assign io_axi_r_payload_last = remainingZero;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      consumeData <= 1'b0;
      sendReadRsp <= 1'b0;
      sendWriteRsp <= 1'b0;
    end else begin
      if(_zz_1_)begin
        consumeData <= io_axi_arw_payload_write;
        sendReadRsp <= (! io_axi_arw_payload_write);
      end
      if(((io_axi_w_valid && io_axi_w_ready) && io_axi_w_payload_last))begin
        consumeData <= 1'b0;
        sendWriteRsp <= 1'b1;
      end
      if((io_axi_b_valid && io_axi_b_ready))begin
        sendWriteRsp <= 1'b0;
      end
      if(sendReadRsp)begin
        if(io_axi_r_ready)begin
          if(remainingZero)begin
            sendReadRsp <= 1'b0;
          end
        end
      end
    end
  end

  always @ (posedge io_clock) begin
    if(_zz_1_)begin
      remaining <= 8'h0;
    end
    if(sendReadRsp)begin
      if(io_axi_r_ready)begin
        remaining <= (remaining - 8'h01);
      end
    end
  end


endmodule

module StreamArbiter (
  input               io_inputs_0_valid,
  output              io_inputs_0_ready,
  input      [16:0]   io_inputs_0_payload_addr,
  input      [2:0]    io_inputs_0_payload_id,
  input      [7:0]    io_inputs_0_payload_len,
  input      [2:0]    io_inputs_0_payload_size,
  input      [1:0]    io_inputs_0_payload_burst,
  input               io_inputs_0_payload_write,
  input               io_inputs_1_valid,
  output              io_inputs_1_ready,
  input      [16:0]   io_inputs_1_payload_addr,
  input      [2:0]    io_inputs_1_payload_id,
  input      [7:0]    io_inputs_1_payload_len,
  input      [2:0]    io_inputs_1_payload_size,
  input      [1:0]    io_inputs_1_payload_burst,
  input               io_inputs_1_payload_write,
  output              io_output_valid,
  input               io_output_ready,
  output     [16:0]   io_output_payload_addr,
  output     [2:0]    io_output_payload_id,
  output     [7:0]    io_output_payload_len,
  output     [2:0]    io_output_payload_size,
  output     [1:0]    io_output_payload_burst,
  output              io_output_payload_write,
  output     [0:0]    io_chosen,
  output     [1:0]    io_chosenOH,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire       [3:0]    _zz_6_;
  wire       [1:0]    _zz_7_;
  wire       [3:0]    _zz_8_;
  wire       [0:0]    _zz_9_;
  wire       [0:0]    _zz_10_;
  reg                 locked;
  wire                maskProposal_0;
  wire                maskProposal_1;
  reg                 maskLocked_0;
  reg                 maskLocked_1;
  wire                maskRouted_0;
  wire                maskRouted_1;
  wire       [1:0]    _zz_1_;
  wire       [3:0]    _zz_2_;
  wire       [3:0]    _zz_3_;
  wire       [1:0]    _zz_4_;
  wire                _zz_5_;

  assign _zz_6_ = (_zz_2_ - _zz_8_);
  assign _zz_7_ = {maskLocked_0,maskLocked_1};
  assign _zz_8_ = {2'd0, _zz_7_};
  assign _zz_9_ = _zz_4_[0 : 0];
  assign _zz_10_ = _zz_4_[1 : 1];
  assign maskRouted_0 = (locked ? maskLocked_0 : maskProposal_0);
  assign maskRouted_1 = (locked ? maskLocked_1 : maskProposal_1);
  assign _zz_1_ = {io_inputs_1_valid,io_inputs_0_valid};
  assign _zz_2_ = {_zz_1_,_zz_1_};
  assign _zz_3_ = (_zz_2_ & (~ _zz_6_));
  assign _zz_4_ = (_zz_3_[3 : 2] | _zz_3_[1 : 0]);
  assign maskProposal_0 = _zz_9_[0];
  assign maskProposal_1 = _zz_10_[0];
  assign io_output_valid = ((io_inputs_0_valid && maskRouted_0) || (io_inputs_1_valid && maskRouted_1));
  assign io_output_payload_addr = (maskRouted_0 ? io_inputs_0_payload_addr : io_inputs_1_payload_addr);
  assign io_output_payload_id = (maskRouted_0 ? io_inputs_0_payload_id : io_inputs_1_payload_id);
  assign io_output_payload_len = (maskRouted_0 ? io_inputs_0_payload_len : io_inputs_1_payload_len);
  assign io_output_payload_size = (maskRouted_0 ? io_inputs_0_payload_size : io_inputs_1_payload_size);
  assign io_output_payload_burst = (maskRouted_0 ? io_inputs_0_payload_burst : io_inputs_1_payload_burst);
  assign io_output_payload_write = (maskRouted_0 ? io_inputs_0_payload_write : io_inputs_1_payload_write);
  assign io_inputs_0_ready = (maskRouted_0 && io_output_ready);
  assign io_inputs_1_ready = (maskRouted_1 && io_output_ready);
  assign io_chosenOH = {maskRouted_1,maskRouted_0};
  assign _zz_5_ = io_chosenOH[1];
  assign io_chosen = _zz_5_;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      locked <= 1'b0;
      maskLocked_0 <= 1'b0;
      maskLocked_1 <= 1'b1;
    end else begin
      if(io_output_valid)begin
        maskLocked_0 <= maskRouted_0;
        maskLocked_1 <= maskRouted_1;
      end
      if(io_output_valid)begin
        locked <= 1'b1;
      end
      if((io_output_valid && io_output_ready))begin
        locked <= 1'b0;
      end
    end
  end


endmodule

module StreamFork (
  input               io_input_valid,
  output reg          io_input_ready,
  input      [16:0]   io_input_payload_addr,
  input      [2:0]    io_input_payload_id,
  input      [7:0]    io_input_payload_len,
  input      [2:0]    io_input_payload_size,
  input      [1:0]    io_input_payload_burst,
  input               io_input_payload_write,
  output              io_outputs_0_valid,
  input               io_outputs_0_ready,
  output     [16:0]   io_outputs_0_payload_addr,
  output     [2:0]    io_outputs_0_payload_id,
  output     [7:0]    io_outputs_0_payload_len,
  output     [2:0]    io_outputs_0_payload_size,
  output     [1:0]    io_outputs_0_payload_burst,
  output              io_outputs_0_payload_write,
  output              io_outputs_1_valid,
  input               io_outputs_1_ready,
  output     [16:0]   io_outputs_1_payload_addr,
  output     [2:0]    io_outputs_1_payload_id,
  output     [7:0]    io_outputs_1_payload_len,
  output     [2:0]    io_outputs_1_payload_size,
  output     [1:0]    io_outputs_1_payload_burst,
  output              io_outputs_1_payload_write,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg                 _zz_1_;
  reg                 _zz_2_;

  always @ (*) begin
    io_input_ready = 1'b1;
    if(((! io_outputs_0_ready) && _zz_1_))begin
      io_input_ready = 1'b0;
    end
    if(((! io_outputs_1_ready) && _zz_2_))begin
      io_input_ready = 1'b0;
    end
  end

  assign io_outputs_0_valid = (io_input_valid && _zz_1_);
  assign io_outputs_0_payload_addr = io_input_payload_addr;
  assign io_outputs_0_payload_id = io_input_payload_id;
  assign io_outputs_0_payload_len = io_input_payload_len;
  assign io_outputs_0_payload_size = io_input_payload_size;
  assign io_outputs_0_payload_burst = io_input_payload_burst;
  assign io_outputs_0_payload_write = io_input_payload_write;
  assign io_outputs_1_valid = (io_input_valid && _zz_2_);
  assign io_outputs_1_payload_addr = io_input_payload_addr;
  assign io_outputs_1_payload_id = io_input_payload_id;
  assign io_outputs_1_payload_len = io_input_payload_len;
  assign io_outputs_1_payload_size = io_input_payload_size;
  assign io_outputs_1_payload_burst = io_input_payload_burst;
  assign io_outputs_1_payload_write = io_input_payload_write;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      _zz_1_ <= 1'b1;
      _zz_2_ <= 1'b1;
    end else begin
      if((io_outputs_0_valid && io_outputs_0_ready))begin
        _zz_1_ <= 1'b0;
      end
      if((io_outputs_1_valid && io_outputs_1_ready))begin
        _zz_2_ <= 1'b0;
      end
      if(io_input_ready)begin
        _zz_1_ <= 1'b1;
        _zz_2_ <= 1'b1;
      end
    end
  end


endmodule

module StreamFifoLowLatency_1_ (
  input               io_push_valid,
  output              io_push_ready,
  output reg          io_pop_valid,
  input               io_pop_ready,
  input               io_flush,
  output     [2:0]    io_occupancy,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire       [0:0]    _zz_1_;
  wire       [1:0]    _zz_2_;
  wire       [0:0]    _zz_3_;
  wire       [1:0]    _zz_4_;
  reg                 pushPtr_willIncrement;
  reg                 pushPtr_willClear;
  reg        [1:0]    pushPtr_valueNext;
  reg        [1:0]    pushPtr_value;
  wire                pushPtr_willOverflowIfInc;
  wire                pushPtr_willOverflow;
  reg                 popPtr_willIncrement;
  reg                 popPtr_willClear;
  reg        [1:0]    popPtr_valueNext;
  reg        [1:0]    popPtr_value;
  wire                popPtr_willOverflowIfInc;
  wire                popPtr_willOverflow;
  wire                ptrMatch;
  reg                 risingOccupancy;
  wire                empty;
  wire                full;
  wire                pushing;
  wire                popping;
  wire       [1:0]    ptrDif;

  assign _zz_1_ = pushPtr_willIncrement;
  assign _zz_2_ = {1'd0, _zz_1_};
  assign _zz_3_ = popPtr_willIncrement;
  assign _zz_4_ = {1'd0, _zz_3_};
  always @ (*) begin
    pushPtr_willIncrement = 1'b0;
    if(pushing)begin
      pushPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    pushPtr_willClear = 1'b0;
    if(io_flush)begin
      pushPtr_willClear = 1'b1;
    end
  end

  assign pushPtr_willOverflowIfInc = (pushPtr_value == (2'b11));
  assign pushPtr_willOverflow = (pushPtr_willOverflowIfInc && pushPtr_willIncrement);
  always @ (*) begin
    pushPtr_valueNext = (pushPtr_value + _zz_2_);
    if(pushPtr_willClear)begin
      pushPtr_valueNext = (2'b00);
    end
  end

  always @ (*) begin
    popPtr_willIncrement = 1'b0;
    if(popping)begin
      popPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    popPtr_willClear = 1'b0;
    if(io_flush)begin
      popPtr_willClear = 1'b1;
    end
  end

  assign popPtr_willOverflowIfInc = (popPtr_value == (2'b11));
  assign popPtr_willOverflow = (popPtr_willOverflowIfInc && popPtr_willIncrement);
  always @ (*) begin
    popPtr_valueNext = (popPtr_value + _zz_4_);
    if(popPtr_willClear)begin
      popPtr_valueNext = (2'b00);
    end
  end

  assign ptrMatch = (pushPtr_value == popPtr_value);
  assign empty = (ptrMatch && (! risingOccupancy));
  assign full = (ptrMatch && risingOccupancy);
  assign pushing = (io_push_valid && io_push_ready);
  assign popping = (io_pop_valid && io_pop_ready);
  assign io_push_ready = (! full);
  always @ (*) begin
    if((! empty))begin
      io_pop_valid = 1'b1;
    end else begin
      io_pop_valid = io_push_valid;
    end
  end

  assign ptrDif = (pushPtr_value - popPtr_value);
  assign io_occupancy = {(risingOccupancy && ptrMatch),ptrDif};
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      pushPtr_value <= (2'b00);
      popPtr_value <= (2'b00);
      risingOccupancy <= 1'b0;
    end else begin
      pushPtr_value <= pushPtr_valueNext;
      popPtr_value <= popPtr_valueNext;
      if((pushing != popping))begin
        risingOccupancy <= pushing;
      end
      if(io_flush)begin
        risingOccupancy <= 1'b0;
      end
    end
  end


endmodule

module StreamArbiter_1_ (
  input               io_inputs_0_valid,
  output              io_inputs_0_ready,
  input      [19:0]   io_inputs_0_payload_addr,
  input      [3:0]    io_inputs_0_payload_id,
  input      [7:0]    io_inputs_0_payload_len,
  input      [2:0]    io_inputs_0_payload_size,
  input      [1:0]    io_inputs_0_payload_burst,
  input               io_inputs_0_payload_write,
  output              io_output_valid,
  input               io_output_ready,
  output     [19:0]   io_output_payload_addr,
  output     [3:0]    io_output_payload_id,
  output     [7:0]    io_output_payload_len,
  output     [2:0]    io_output_payload_size,
  output     [1:0]    io_output_payload_burst,
  output              io_output_payload_write,
  output     [0:0]    io_chosenOH,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire       [1:0]    _zz_4_;
  wire       [0:0]    _zz_5_;
  wire       [1:0]    _zz_6_;
  wire       [0:0]    _zz_7_;
  wire       [0:0]    _zz_8_;
  reg                 locked;
  wire                maskProposal_0;
  reg                 maskLocked_0;
  wire                maskRouted_0;
  wire       [0:0]    _zz_1_;
  wire       [1:0]    _zz_2_;
  wire       [1:0]    _zz_3_;

  assign _zz_4_ = (_zz_2_ - _zz_6_);
  assign _zz_5_ = maskLocked_0;
  assign _zz_6_ = {1'd0, _zz_5_};
  assign _zz_7_ = _zz_8_[0 : 0];
  assign _zz_8_ = (_zz_3_[1 : 1] | _zz_3_[0 : 0]);
  assign maskRouted_0 = (locked ? maskLocked_0 : maskProposal_0);
  assign _zz_1_ = io_inputs_0_valid;
  assign _zz_2_ = {_zz_1_,_zz_1_};
  assign _zz_3_ = (_zz_2_ & (~ _zz_4_));
  assign maskProposal_0 = _zz_7_[0];
  assign io_output_valid = (io_inputs_0_valid && maskRouted_0);
  assign io_output_payload_addr = io_inputs_0_payload_addr;
  assign io_output_payload_id = io_inputs_0_payload_id;
  assign io_output_payload_len = io_inputs_0_payload_len;
  assign io_output_payload_size = io_inputs_0_payload_size;
  assign io_output_payload_burst = io_inputs_0_payload_burst;
  assign io_output_payload_write = io_inputs_0_payload_write;
  assign io_inputs_0_ready = (maskRouted_0 && io_output_ready);
  assign io_chosenOH = maskRouted_0;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      locked <= 1'b0;
      maskLocked_0 <= 1'b1;
    end else begin
      if(io_output_valid)begin
        maskLocked_0 <= maskRouted_0;
      end
      if(io_output_valid)begin
        locked <= 1'b1;
      end
      if((io_output_valid && io_output_ready))begin
        locked <= 1'b0;
      end
    end
  end


endmodule

module StreamFork_1_ (
  input               io_input_valid,
  output reg          io_input_ready,
  input      [19:0]   io_input_payload_addr,
  input      [3:0]    io_input_payload_id,
  input      [7:0]    io_input_payload_len,
  input      [2:0]    io_input_payload_size,
  input      [1:0]    io_input_payload_burst,
  input               io_input_payload_write,
  output              io_outputs_0_valid,
  input               io_outputs_0_ready,
  output     [19:0]   io_outputs_0_payload_addr,
  output     [3:0]    io_outputs_0_payload_id,
  output     [7:0]    io_outputs_0_payload_len,
  output     [2:0]    io_outputs_0_payload_size,
  output     [1:0]    io_outputs_0_payload_burst,
  output              io_outputs_0_payload_write,
  output              io_outputs_1_valid,
  input               io_outputs_1_ready,
  output     [19:0]   io_outputs_1_payload_addr,
  output     [3:0]    io_outputs_1_payload_id,
  output     [7:0]    io_outputs_1_payload_len,
  output     [2:0]    io_outputs_1_payload_size,
  output     [1:0]    io_outputs_1_payload_burst,
  output              io_outputs_1_payload_write,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg                 _zz_1_;
  reg                 _zz_2_;

  always @ (*) begin
    io_input_ready = 1'b1;
    if(((! io_outputs_0_ready) && _zz_1_))begin
      io_input_ready = 1'b0;
    end
    if(((! io_outputs_1_ready) && _zz_2_))begin
      io_input_ready = 1'b0;
    end
  end

  assign io_outputs_0_valid = (io_input_valid && _zz_1_);
  assign io_outputs_0_payload_addr = io_input_payload_addr;
  assign io_outputs_0_payload_id = io_input_payload_id;
  assign io_outputs_0_payload_len = io_input_payload_len;
  assign io_outputs_0_payload_size = io_input_payload_size;
  assign io_outputs_0_payload_burst = io_input_payload_burst;
  assign io_outputs_0_payload_write = io_input_payload_write;
  assign io_outputs_1_valid = (io_input_valid && _zz_2_);
  assign io_outputs_1_payload_addr = io_input_payload_addr;
  assign io_outputs_1_payload_id = io_input_payload_id;
  assign io_outputs_1_payload_len = io_input_payload_len;
  assign io_outputs_1_payload_size = io_input_payload_size;
  assign io_outputs_1_payload_burst = io_input_payload_burst;
  assign io_outputs_1_payload_write = io_input_payload_write;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      _zz_1_ <= 1'b1;
      _zz_2_ <= 1'b1;
    end else begin
      if((io_outputs_0_valid && io_outputs_0_ready))begin
        _zz_1_ <= 1'b0;
      end
      if((io_outputs_1_valid && io_outputs_1_ready))begin
        _zz_2_ <= 1'b0;
      end
      if(io_input_ready)begin
        _zz_1_ <= 1'b1;
        _zz_2_ <= 1'b1;
      end
    end
  end


endmodule
//StreamFifoLowLatency_2_ replaced by StreamFifoLowLatency_1_

module MachineTimerCtrl (
  input      [63:0]   io_config_compare,
  output     [63:0]   io_counter,
  input               io_clear,
  output              io_interrupt,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire       [63:0]   _zz_1_;
  reg        [63:0]   counter;
  reg                 hit;
  reg                 lock;

  assign _zz_1_ = (counter - io_config_compare);
  assign io_counter = counter;
  assign io_interrupt = hit;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      counter <= 64'h0000000000000001;
      hit <= 1'b0;
      lock <= 1'b1;
    end else begin
      if(io_clear)begin
        lock <= 1'b0;
      end
      if((! lock))begin
        counter <= (counter + 64'h0000000000000001);
      end
      if((io_clear || lock))begin
        hit <= 1'b0;
      end else begin
        if((! _zz_1_[63]))begin
          hit <= 1'b1;
        end
      end
    end
  end


endmodule

module UartCtrl (
  input      [19:0]   io_config_clockDivider,
  input      `ParityType_defaultEncoding_type io_frameConfig_parity,
  input      `StopType_defaultEncoding_type io_frameConfig_stop,
  input      [3:0]    io_frameConfig_dataLength,
  output              io_uart_txd,
  input               io_uart_rxd,
  output              io_interrupt,
  input      [1:0]    io_pendingInterrupts,
  input               io_write_valid,
  output              io_write_ready,
  input      [8:0]    io_write_payload,
  output              io_read_valid,
  input               io_read_ready,
  output     [8:0]    io_read_payload,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                tx_io_write_ready;
  wire                tx_io_txd;
  wire                rx_io_read_valid;
  wire       [8:0]    rx_io_read_payload;
  reg        [19:0]   clockDivider_counter;
  wire                clockDivider_tick;
  `ifndef SYNTHESIS
  reg [31:0] io_frameConfig_parity_string;
  reg [23:0] io_frameConfig_stop_string;
  `endif


  UartCtrlTx tx ( 
    .io_config_parity         (io_frameConfig_parity[1:0]      ), //i
    .io_config_stop           (io_frameConfig_stop             ), //i
    .io_config_dataLength     (io_frameConfig_dataLength[3:0]  ), //i
    .io_samplingTick          (clockDivider_tick               ), //i
    .io_write_valid           (io_write_valid                  ), //i
    .io_write_ready           (tx_io_write_ready               ), //o
    .io_write_payload         (io_write_payload[8:0]           ), //i
    .io_txd                   (tx_io_txd                       ), //o
    .io_clock                 (io_clock                        ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset           )  //i
  );
  UartCtrlRx rx ( 
    .io_config_parity         (io_frameConfig_parity[1:0]      ), //i
    .io_config_stop           (io_frameConfig_stop             ), //i
    .io_config_dataLength     (io_frameConfig_dataLength[3:0]  ), //i
    .io_samplingTick          (clockDivider_tick               ), //i
    .io_read_valid            (rx_io_read_valid                ), //o
    .io_read_ready            (io_read_ready                   ), //i
    .io_read_payload          (rx_io_read_payload[8:0]         ), //o
    .io_rxd                   (io_uart_rxd                     ), //i
    .io_clock                 (io_clock                        ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset           )  //i
  );
  `ifndef SYNTHESIS
  always @(*) begin
    case(io_frameConfig_parity)
      `ParityType_defaultEncoding_NONE : io_frameConfig_parity_string = "NONE";
      `ParityType_defaultEncoding_EVEN : io_frameConfig_parity_string = "EVEN";
      `ParityType_defaultEncoding_ODD : io_frameConfig_parity_string = "ODD ";
      default : io_frameConfig_parity_string = "????";
    endcase
  end
  always @(*) begin
    case(io_frameConfig_stop)
      `StopType_defaultEncoding_ONE : io_frameConfig_stop_string = "ONE";
      `StopType_defaultEncoding_TWO : io_frameConfig_stop_string = "TWO";
      default : io_frameConfig_stop_string = "???";
    endcase
  end
  `endif

  assign clockDivider_tick = (clockDivider_counter == 20'h0);
  assign io_interrupt = (io_pendingInterrupts != (2'b00));
  assign io_write_ready = tx_io_write_ready;
  assign io_uart_txd = tx_io_txd;
  assign io_read_valid = rx_io_read_valid;
  assign io_read_payload = rx_io_read_payload;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      clockDivider_counter <= 20'h0;
    end else begin
      clockDivider_counter <= (clockDivider_counter - 20'h00001);
      if(clockDivider_tick)begin
        clockDivider_counter <= io_config_clockDivider;
      end
    end
  end


endmodule

module StreamFifo (
  input               io_push_valid,
  output              io_push_ready,
  input      [8:0]    io_push_payload,
  output              io_pop_valid,
  input               io_pop_ready,
  output     [8:0]    io_pop_payload,
  input               io_flush,
  output     [4:0]    io_occupancy,
  output     [4:0]    io_availability,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [8:0]    _zz_3_;
  wire       [0:0]    _zz_4_;
  wire       [3:0]    _zz_5_;
  wire       [0:0]    _zz_6_;
  wire       [3:0]    _zz_7_;
  wire       [3:0]    _zz_8_;
  wire                _zz_9_;
  reg                 _zz_1_;
  reg                 logic_pushPtr_willIncrement;
  reg                 logic_pushPtr_willClear;
  reg        [3:0]    logic_pushPtr_valueNext;
  reg        [3:0]    logic_pushPtr_value;
  wire                logic_pushPtr_willOverflowIfInc;
  wire                logic_pushPtr_willOverflow;
  reg                 logic_popPtr_willIncrement;
  reg                 logic_popPtr_willClear;
  reg        [3:0]    logic_popPtr_valueNext;
  reg        [3:0]    logic_popPtr_value;
  wire                logic_popPtr_willOverflowIfInc;
  wire                logic_popPtr_willOverflow;
  wire                logic_ptrMatch;
  reg                 logic_risingOccupancy;
  wire                logic_pushing;
  wire                logic_popping;
  wire                logic_empty;
  wire                logic_full;
  reg                 _zz_2_;
  wire       [3:0]    logic_ptrDif;
  reg [8:0] logic_ram [0:15];

  assign _zz_4_ = logic_pushPtr_willIncrement;
  assign _zz_5_ = {3'd0, _zz_4_};
  assign _zz_6_ = logic_popPtr_willIncrement;
  assign _zz_7_ = {3'd0, _zz_6_};
  assign _zz_8_ = (logic_popPtr_value - logic_pushPtr_value);
  assign _zz_9_ = 1'b1;
  always @ (posedge io_clock) begin
    if(_zz_9_) begin
      _zz_3_ <= logic_ram[logic_popPtr_valueNext];
    end
  end

  always @ (posedge io_clock) begin
    if(_zz_1_) begin
      logic_ram[logic_pushPtr_value] <= io_push_payload;
    end
  end

  always @ (*) begin
    _zz_1_ = 1'b0;
    if(logic_pushing)begin
      _zz_1_ = 1'b1;
    end
  end

  always @ (*) begin
    logic_pushPtr_willIncrement = 1'b0;
    if(logic_pushing)begin
      logic_pushPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    logic_pushPtr_willClear = 1'b0;
    if(io_flush)begin
      logic_pushPtr_willClear = 1'b1;
    end
  end

  assign logic_pushPtr_willOverflowIfInc = (logic_pushPtr_value == (4'b1111));
  assign logic_pushPtr_willOverflow = (logic_pushPtr_willOverflowIfInc && logic_pushPtr_willIncrement);
  always @ (*) begin
    logic_pushPtr_valueNext = (logic_pushPtr_value + _zz_5_);
    if(logic_pushPtr_willClear)begin
      logic_pushPtr_valueNext = (4'b0000);
    end
  end

  always @ (*) begin
    logic_popPtr_willIncrement = 1'b0;
    if(logic_popping)begin
      logic_popPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    logic_popPtr_willClear = 1'b0;
    if(io_flush)begin
      logic_popPtr_willClear = 1'b1;
    end
  end

  assign logic_popPtr_willOverflowIfInc = (logic_popPtr_value == (4'b1111));
  assign logic_popPtr_willOverflow = (logic_popPtr_willOverflowIfInc && logic_popPtr_willIncrement);
  always @ (*) begin
    logic_popPtr_valueNext = (logic_popPtr_value + _zz_7_);
    if(logic_popPtr_willClear)begin
      logic_popPtr_valueNext = (4'b0000);
    end
  end

  assign logic_ptrMatch = (logic_pushPtr_value == logic_popPtr_value);
  assign logic_pushing = (io_push_valid && io_push_ready);
  assign logic_popping = (io_pop_valid && io_pop_ready);
  assign logic_empty = (logic_ptrMatch && (! logic_risingOccupancy));
  assign logic_full = (logic_ptrMatch && logic_risingOccupancy);
  assign io_push_ready = (! logic_full);
  assign io_pop_valid = ((! logic_empty) && (! (_zz_2_ && (! logic_full))));
  assign io_pop_payload = _zz_3_;
  assign logic_ptrDif = (logic_pushPtr_value - logic_popPtr_value);
  assign io_occupancy = {(logic_risingOccupancy && logic_ptrMatch),logic_ptrDif};
  assign io_availability = {((! logic_risingOccupancy) && logic_ptrMatch),_zz_8_};
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      logic_pushPtr_value <= (4'b0000);
      logic_popPtr_value <= (4'b0000);
      logic_risingOccupancy <= 1'b0;
      _zz_2_ <= 1'b0;
    end else begin
      logic_pushPtr_value <= logic_pushPtr_valueNext;
      logic_popPtr_value <= logic_popPtr_valueNext;
      _zz_2_ <= (logic_popPtr_valueNext == logic_pushPtr_value);
      if((logic_pushing != logic_popping))begin
        logic_risingOccupancy <= logic_pushing;
      end
      if(io_flush)begin
        logic_risingOccupancy <= 1'b0;
      end
    end
  end


endmodule
//StreamFifo_1_ replaced by StreamFifo

module InterruptCtrl (
  input      [1:0]    io_inputs,
  input      [1:0]    io_clears,
  input      [1:0]    io_masks,
  output     [1:0]    io_pendings,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [1:0]    pendings;

  assign io_pendings = (pendings & io_masks);
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      pendings <= (2'b00);
    end else begin
      pendings <= ((pendings & (~ io_clears)) | io_inputs);
    end
  end


endmodule
//UartCtrl_1_ replaced by UartCtrl

module StreamFifo_2_ (
  input               io_push_valid,
  output              io_push_ready,
  input      [8:0]    io_push_payload,
  output              io_pop_valid,
  input               io_pop_ready,
  output     [8:0]    io_pop_payload,
  input               io_flush,
  output     [6:0]    io_occupancy,
  output     [6:0]    io_availability,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [8:0]    _zz_3_;
  wire       [0:0]    _zz_4_;
  wire       [5:0]    _zz_5_;
  wire       [0:0]    _zz_6_;
  wire       [5:0]    _zz_7_;
  wire       [5:0]    _zz_8_;
  wire                _zz_9_;
  reg                 _zz_1_;
  reg                 logic_pushPtr_willIncrement;
  reg                 logic_pushPtr_willClear;
  reg        [5:0]    logic_pushPtr_valueNext;
  reg        [5:0]    logic_pushPtr_value;
  wire                logic_pushPtr_willOverflowIfInc;
  wire                logic_pushPtr_willOverflow;
  reg                 logic_popPtr_willIncrement;
  reg                 logic_popPtr_willClear;
  reg        [5:0]    logic_popPtr_valueNext;
  reg        [5:0]    logic_popPtr_value;
  wire                logic_popPtr_willOverflowIfInc;
  wire                logic_popPtr_willOverflow;
  wire                logic_ptrMatch;
  reg                 logic_risingOccupancy;
  wire                logic_pushing;
  wire                logic_popping;
  wire                logic_empty;
  wire                logic_full;
  reg                 _zz_2_;
  wire       [5:0]    logic_ptrDif;
  reg [8:0] logic_ram [0:63];

  assign _zz_4_ = logic_pushPtr_willIncrement;
  assign _zz_5_ = {5'd0, _zz_4_};
  assign _zz_6_ = logic_popPtr_willIncrement;
  assign _zz_7_ = {5'd0, _zz_6_};
  assign _zz_8_ = (logic_popPtr_value - logic_pushPtr_value);
  assign _zz_9_ = 1'b1;
  always @ (posedge io_clock) begin
    if(_zz_9_) begin
      _zz_3_ <= logic_ram[logic_popPtr_valueNext];
    end
  end

  always @ (posedge io_clock) begin
    if(_zz_1_) begin
      logic_ram[logic_pushPtr_value] <= io_push_payload;
    end
  end

  always @ (*) begin
    _zz_1_ = 1'b0;
    if(logic_pushing)begin
      _zz_1_ = 1'b1;
    end
  end

  always @ (*) begin
    logic_pushPtr_willIncrement = 1'b0;
    if(logic_pushing)begin
      logic_pushPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    logic_pushPtr_willClear = 1'b0;
    if(io_flush)begin
      logic_pushPtr_willClear = 1'b1;
    end
  end

  assign logic_pushPtr_willOverflowIfInc = (logic_pushPtr_value == 6'h3f);
  assign logic_pushPtr_willOverflow = (logic_pushPtr_willOverflowIfInc && logic_pushPtr_willIncrement);
  always @ (*) begin
    logic_pushPtr_valueNext = (logic_pushPtr_value + _zz_5_);
    if(logic_pushPtr_willClear)begin
      logic_pushPtr_valueNext = 6'h0;
    end
  end

  always @ (*) begin
    logic_popPtr_willIncrement = 1'b0;
    if(logic_popping)begin
      logic_popPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    logic_popPtr_willClear = 1'b0;
    if(io_flush)begin
      logic_popPtr_willClear = 1'b1;
    end
  end

  assign logic_popPtr_willOverflowIfInc = (logic_popPtr_value == 6'h3f);
  assign logic_popPtr_willOverflow = (logic_popPtr_willOverflowIfInc && logic_popPtr_willIncrement);
  always @ (*) begin
    logic_popPtr_valueNext = (logic_popPtr_value + _zz_7_);
    if(logic_popPtr_willClear)begin
      logic_popPtr_valueNext = 6'h0;
    end
  end

  assign logic_ptrMatch = (logic_pushPtr_value == logic_popPtr_value);
  assign logic_pushing = (io_push_valid && io_push_ready);
  assign logic_popping = (io_pop_valid && io_pop_ready);
  assign logic_empty = (logic_ptrMatch && (! logic_risingOccupancy));
  assign logic_full = (logic_ptrMatch && logic_risingOccupancy);
  assign io_push_ready = (! logic_full);
  assign io_pop_valid = ((! logic_empty) && (! (_zz_2_ && (! logic_full))));
  assign io_pop_payload = _zz_3_;
  assign logic_ptrDif = (logic_pushPtr_value - logic_popPtr_value);
  assign io_occupancy = {(logic_risingOccupancy && logic_ptrMatch),logic_ptrDif};
  assign io_availability = {((! logic_risingOccupancy) && logic_ptrMatch),_zz_8_};
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      logic_pushPtr_value <= 6'h0;
      logic_popPtr_value <= 6'h0;
      logic_risingOccupancy <= 1'b0;
      _zz_2_ <= 1'b0;
    end else begin
      logic_pushPtr_value <= logic_pushPtr_valueNext;
      logic_popPtr_value <= logic_popPtr_valueNext;
      _zz_2_ <= (logic_popPtr_valueNext == logic_pushPtr_value);
      if((logic_pushing != logic_popping))begin
        logic_risingOccupancy <= logic_pushing;
      end
      if(io_flush)begin
        logic_risingOccupancy <= 1'b0;
      end
    end
  end


endmodule
//StreamFifo_3_ replaced by StreamFifo_2_
//InterruptCtrl_1_ replaced by InterruptCtrl
//UartCtrl_2_ replaced by UartCtrl
//StreamFifo_4_ replaced by StreamFifo_2_
//StreamFifo_5_ replaced by StreamFifo_2_
//InterruptCtrl_2_ replaced by InterruptCtrl

module GpioCtrl (
  input      [2:0]    io_gpio_pins_read,
  output     [2:0]    io_gpio_pins_write,
  output     [2:0]    io_gpio_pins_writeEnable,
  input      [2:0]    io_config_write,
  input      [2:0]    io_config_direction,
  output     [2:0]    io_value,
  input      [2:0]    io_enable_high,
  input      [2:0]    io_enable_low,
  input      [2:0]    io_enable_rise,
  input      [2:0]    io_enable_fall,
  output     [2:0]    io_interrupt,
  output     [2:0]    io_irqHigh_valid,
  input      [2:0]    io_irqHigh_pending,
  output     [2:0]    io_irqLow_valid,
  input      [2:0]    io_irqLow_pending,
  output     [2:0]    io_irqRise_valid,
  input      [2:0]    io_irqRise_pending,
  output     [2:0]    io_irqFall_valid,
  input      [2:0]    io_irqFall_pending,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire       [2:0]    io_gpio_pins_read_buffercc_io_dataOut;
  wire       [2:0]    io_gpio_pins_read_buffercc_1__io_dataOut;
  wire       [2:0]    synchronized;
  reg        [2:0]    last;

  BufferCC_4_ io_gpio_pins_read_buffercc ( 
    .io_dataIn                (io_gpio_pins_read[2:0]                      ), //i
    .io_dataOut               (io_gpio_pins_read_buffercc_io_dataOut[2:0]  ), //o
    .io_clock                 (io_clock                                    ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                       )  //i
  );
  BufferCC_4_ io_gpio_pins_read_buffercc_1_ ( 
    .io_dataIn                (io_gpio_pins_read[2:0]                         ), //i
    .io_dataOut               (io_gpio_pins_read_buffercc_1__io_dataOut[2:0]  ), //o
    .io_clock                 (io_clock                                       ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                          )  //i
  );
  assign io_value = io_gpio_pins_read_buffercc_io_dataOut;
  assign io_gpio_pins_write = io_config_write;
  assign io_gpio_pins_writeEnable = io_config_direction;
  assign synchronized = io_gpio_pins_read_buffercc_1__io_dataOut;
  assign io_irqHigh_valid = synchronized;
  assign io_irqLow_valid = (~ synchronized);
  assign io_irqRise_valid = (synchronized & (~ last));
  assign io_irqFall_valid = ((~ synchronized) & last);
  assign io_interrupt = (((io_irqHigh_pending | io_irqLow_pending) | io_irqRise_pending) | io_irqFall_pending);
  always @ (posedge io_clock) begin
    last <= synchronized;
  end


endmodule

module InterruptCtrl_3_ (
  input      [2:0]    io_inputs,
  input      [2:0]    io_clears,
  input      [2:0]    io_masks,
  output     [2:0]    io_pendings,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [2:0]    pendings;

  assign io_pendings = (pendings & io_masks);
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      pendings <= (3'b000);
    end else begin
      pendings <= ((pendings & (~ io_clears)) | io_inputs);
    end
  end


endmodule
//InterruptCtrl_4_ replaced by InterruptCtrl_3_
//InterruptCtrl_5_ replaced by InterruptCtrl_3_
//InterruptCtrl_6_ replaced by InterruptCtrl_3_

module GpioCtrl_1_ (
  input      [11:0]   io_gpio_pins_read,
  output     [11:0]   io_gpio_pins_write,
  output     [11:0]   io_gpio_pins_writeEnable,
  input      [11:0]   io_config_write,
  input      [11:0]   io_config_direction,
  output     [11:0]   io_value,
  input      [11:0]   io_enable_high,
  input      [11:0]   io_enable_low,
  input      [11:0]   io_enable_rise,
  input      [11:0]   io_enable_fall,
  output     [11:0]   io_interrupt,
  output     [11:0]   io_irqHigh_valid,
  input      [11:0]   io_irqHigh_pending,
  output     [11:0]   io_irqLow_valid,
  input      [11:0]   io_irqLow_pending,
  output     [11:0]   io_irqRise_valid,
  input      [11:0]   io_irqRise_pending,
  output     [11:0]   io_irqFall_valid,
  input      [11:0]   io_irqFall_pending,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire       [11:0]   io_gpio_pins_read_buffercc_io_dataOut;
  wire       [11:0]   io_gpio_pins_read_buffercc_1__io_dataOut;
  wire       [11:0]   synchronized;
  reg        [11:0]   last;

  BufferCC_6_ io_gpio_pins_read_buffercc ( 
    .io_dataIn                (io_gpio_pins_read[11:0]                      ), //i
    .io_dataOut               (io_gpio_pins_read_buffercc_io_dataOut[11:0]  ), //o
    .io_clock                 (io_clock                                     ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                        )  //i
  );
  BufferCC_6_ io_gpio_pins_read_buffercc_1_ ( 
    .io_dataIn                (io_gpio_pins_read[11:0]                         ), //i
    .io_dataOut               (io_gpio_pins_read_buffercc_1__io_dataOut[11:0]  ), //o
    .io_clock                 (io_clock                                        ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                           )  //i
  );
  assign io_value = io_gpio_pins_read_buffercc_io_dataOut;
  assign io_gpio_pins_write = io_config_write;
  assign io_gpio_pins_writeEnable = io_config_direction;
  assign synchronized = io_gpio_pins_read_buffercc_1__io_dataOut;
  assign io_irqHigh_valid = synchronized;
  assign io_irqLow_valid = (~ synchronized);
  assign io_irqRise_valid = (synchronized & (~ last));
  assign io_irqFall_valid = ((~ synchronized) & last);
  assign io_interrupt = (((io_irqHigh_pending | io_irqLow_pending) | io_irqRise_pending) | io_irqFall_pending);
  always @ (posedge io_clock) begin
    last <= synchronized;
  end


endmodule

module InterruptCtrl_7_ (
  input      [11:0]   io_inputs,
  input      [11:0]   io_clears,
  input      [11:0]   io_masks,
  output     [11:0]   io_pendings,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [11:0]   pendings;

  assign io_pendings = (pendings & io_masks);
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      pendings <= 12'h0;
    end else begin
      pendings <= ((pendings & (~ io_clears)) | io_inputs);
    end
  end


endmodule
//InterruptCtrl_8_ replaced by InterruptCtrl_7_
//InterruptCtrl_9_ replaced by InterruptCtrl_7_
//InterruptCtrl_10_ replaced by InterruptCtrl_7_

module SpiMasterCtrl (
  input      [15:0]   io_config_clockDivider,
  input      [0:0]    io_config_ss_activeHigh,
  input      [15:0]   io_config_ss_setup,
  input      [15:0]   io_config_ss_hold,
  input      [15:0]   io_config_ss_disable,
  input               io_modeConfig_cpol,
  input               io_modeConfig_cpha,
  output     [0:0]    io_spi_ss,
  output              io_spi_sclk,
  output              io_spi_mosi,
  input               io_spi_miso,
  output              io_interrupt,
  input      [1:0]    io_pendingInterrupts,
  input               io_cmd_valid,
  output reg          io_cmd_ready,
  input      `CmdMode_defaultEncoding_type io_cmd_payload_mode,
  input      [8:0]    io_cmd_payload_args,
  output              io_rsp_valid,
  output     [7:0]    io_rsp_payload,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_4_;
  wire                _zz_5_;
  wire                _zz_6_;
  wire                _zz_7_;
  wire       [0:0]    _zz_8_;
  wire       [3:0]    _zz_9_;
  wire       [8:0]    _zz_10_;
  wire       [0:0]    _zz_11_;
  wire       [0:0]    _zz_12_;
  wire       [7:0]    _zz_13_;
  wire       [2:0]    _zz_14_;
  wire       [2:0]    _zz_15_;
  reg        [15:0]   timer_counter;
  reg                 timer_reset;
  wire                timer_ss_setupHit;
  wire                timer_ss_holdHit;
  wire                timer_ss_disableHit;
  wire                timer_clockDividerHit;
  reg                 fsm_counter_willIncrement;
  wire                fsm_counter_willClear;
  reg        [3:0]    fsm_counter_valueNext;
  reg        [3:0]    fsm_counter_value;
  wire                fsm_counter_willOverflowIfInc;
  wire                fsm_counter_willOverflow;
  reg        [7:0]    fsm_buffer;
  reg        [0:0]    fsm_ss;
  reg                 _zz_1_;
  reg                 _zz_2_;
  reg                 _zz_3_;
  `ifndef SYNTHESIS
  reg [31:0] io_cmd_payload_mode_string;
  `endif


  assign _zz_4_ = (io_cmd_payload_mode == `CmdMode_defaultEncoding_DATA);
  assign _zz_5_ = _zz_11_[0];
  assign _zz_6_ = (! fsm_counter_value[0]);
  assign _zz_7_ = ((! io_cmd_valid) || io_cmd_ready);
  assign _zz_8_ = fsm_counter_willIncrement;
  assign _zz_9_ = {3'd0, _zz_8_};
  assign _zz_10_ = {fsm_buffer,io_spi_miso};
  assign _zz_11_ = io_cmd_payload_args[0 : 0];
  assign _zz_12_ = io_cmd_payload_args[8 : 8];
  assign _zz_13_ = io_cmd_payload_args[7 : 0];
  assign _zz_14_ = ((3'b111) - _zz_15_);
  assign _zz_15_ = (fsm_counter_value >>> 1);
  `ifndef SYNTHESIS
  always @(*) begin
    case(io_cmd_payload_mode)
      `CmdMode_defaultEncoding_DATA : io_cmd_payload_mode_string = "DATA";
      `CmdMode_defaultEncoding_SS : io_cmd_payload_mode_string = "SS  ";
      default : io_cmd_payload_mode_string = "????";
    endcase
  end
  `endif

  always @ (*) begin
    timer_reset = 1'b0;
    if(io_cmd_valid)begin
      if(_zz_4_)begin
        if(timer_clockDividerHit)begin
          timer_reset = 1'b1;
        end
      end else begin
        if(! _zz_5_) begin
          if(_zz_6_)begin
            if(timer_ss_holdHit)begin
              timer_reset = 1'b1;
            end
          end
        end
      end
    end
    if(_zz_7_)begin
      timer_reset = 1'b1;
    end
  end

  assign timer_ss_setupHit = (timer_counter == io_config_ss_setup);
  assign timer_ss_holdHit = (timer_counter == io_config_ss_hold);
  assign timer_ss_disableHit = (timer_counter == io_config_ss_disable);
  assign timer_clockDividerHit = (timer_counter == io_config_clockDivider);
  always @ (*) begin
    fsm_counter_willIncrement = 1'b0;
    if(io_cmd_valid)begin
      if(_zz_4_)begin
        if(timer_clockDividerHit)begin
          fsm_counter_willIncrement = 1'b1;
        end
      end else begin
        if(! _zz_5_) begin
          if(_zz_6_)begin
            if(timer_ss_holdHit)begin
              fsm_counter_willIncrement = 1'b1;
            end
          end
        end
      end
    end
  end

  assign fsm_counter_willClear = 1'b0;
  assign fsm_counter_willOverflowIfInc = (fsm_counter_value == (4'b1111));
  assign fsm_counter_willOverflow = (fsm_counter_willOverflowIfInc && fsm_counter_willIncrement);
  always @ (*) begin
    fsm_counter_valueNext = (fsm_counter_value + _zz_9_);
    if(fsm_counter_willClear)begin
      fsm_counter_valueNext = (4'b0000);
    end
  end

  always @ (*) begin
    io_cmd_ready = 1'b0;
    if(io_cmd_valid)begin
      if(_zz_4_)begin
        if(timer_clockDividerHit)begin
          io_cmd_ready = fsm_counter_willOverflowIfInc;
        end
      end else begin
        if(_zz_5_)begin
          if(timer_ss_setupHit)begin
            io_cmd_ready = 1'b1;
          end
        end else begin
          if(! _zz_6_) begin
            if(timer_ss_disableHit)begin
              io_cmd_ready = 1'b1;
            end
          end
        end
      end
    end
  end

  assign io_rsp_valid = _zz_1_;
  assign io_rsp_payload = fsm_buffer;
  assign io_spi_ss = (fsm_ss ^ io_config_ss_activeHigh);
  assign io_spi_sclk = _zz_2_;
  assign io_spi_mosi = _zz_3_;
  assign io_interrupt = (io_pendingInterrupts != (2'b00));
  always @ (posedge io_clock) begin
    timer_counter <= (timer_counter + 16'h0001);
    if(timer_reset)begin
      timer_counter <= 16'h0;
    end
    if(io_cmd_valid)begin
      if(_zz_4_)begin
        if(timer_clockDividerHit)begin
          if(fsm_counter_value[0])begin
            fsm_buffer <= _zz_10_[7:0];
          end
        end
      end
    end
    _zz_2_ <= (((io_cmd_valid && (io_cmd_payload_mode == `CmdMode_defaultEncoding_DATA)) && (fsm_counter_value[0] ^ io_modeConfig_cpha)) ^ io_modeConfig_cpol);
    _zz_3_ <= _zz_13_[_zz_14_];
  end

  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      fsm_counter_value <= (4'b0000);
      fsm_ss <= (1'b1);
      _zz_1_ <= 1'b0;
    end else begin
      fsm_counter_value <= fsm_counter_valueNext;
      if(io_cmd_valid)begin
        if(! _zz_4_) begin
          if(_zz_5_)begin
            fsm_ss[0] <= 1'b0;
          end else begin
            if(! _zz_6_) begin
              fsm_ss[0] <= 1'b1;
            end
          end
        end
      end
      _zz_1_ <= (((io_cmd_valid && io_cmd_ready) && (io_cmd_payload_mode == `CmdMode_defaultEncoding_DATA)) && _zz_12_[0]);
      if(_zz_7_)begin
        fsm_counter_value <= (4'b0000);
      end
    end
  end


endmodule

module StreamFifo_6_ (
  input               io_push_valid,
  output              io_push_ready,
  input      `CmdMode_defaultEncoding_type io_push_payload_mode,
  input      [8:0]    io_push_payload_args,
  output              io_pop_valid,
  input               io_pop_ready,
  output     `CmdMode_defaultEncoding_type io_pop_payload_mode,
  output     [8:0]    io_pop_payload_args,
  input               io_flush,
  output     [4:0]    io_occupancy,
  output     [4:0]    io_availability,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [9:0]    _zz_6_;
  wire       [0:0]    _zz_7_;
  wire       [3:0]    _zz_8_;
  wire       [0:0]    _zz_9_;
  wire       [3:0]    _zz_10_;
  wire       [3:0]    _zz_11_;
  wire                _zz_12_;
  wire       [9:0]    _zz_13_;
  reg                 _zz_1_;
  reg                 logic_pushPtr_willIncrement;
  reg                 logic_pushPtr_willClear;
  reg        [3:0]    logic_pushPtr_valueNext;
  reg        [3:0]    logic_pushPtr_value;
  wire                logic_pushPtr_willOverflowIfInc;
  wire                logic_pushPtr_willOverflow;
  reg                 logic_popPtr_willIncrement;
  reg                 logic_popPtr_willClear;
  reg        [3:0]    logic_popPtr_valueNext;
  reg        [3:0]    logic_popPtr_value;
  wire                logic_popPtr_willOverflowIfInc;
  wire                logic_popPtr_willOverflow;
  wire                logic_ptrMatch;
  reg                 logic_risingOccupancy;
  wire                logic_pushing;
  wire                logic_popping;
  wire                logic_empty;
  wire                logic_full;
  reg                 _zz_2_;
  wire       `CmdMode_defaultEncoding_type _zz_3_;
  wire       [9:0]    _zz_4_;
  wire       `CmdMode_defaultEncoding_type _zz_5_;
  wire       [3:0]    logic_ptrDif;
  `ifndef SYNTHESIS
  reg [31:0] io_push_payload_mode_string;
  reg [31:0] io_pop_payload_mode_string;
  reg [31:0] _zz_3__string;
  reg [31:0] _zz_5__string;
  `endif

  reg [9:0] logic_ram [0:15];

  assign _zz_7_ = logic_pushPtr_willIncrement;
  assign _zz_8_ = {3'd0, _zz_7_};
  assign _zz_9_ = logic_popPtr_willIncrement;
  assign _zz_10_ = {3'd0, _zz_9_};
  assign _zz_11_ = (logic_popPtr_value - logic_pushPtr_value);
  assign _zz_12_ = 1'b1;
  assign _zz_13_ = {io_push_payload_args,io_push_payload_mode};
  always @ (posedge io_clock) begin
    if(_zz_12_) begin
      _zz_6_ <= logic_ram[logic_popPtr_valueNext];
    end
  end

  always @ (posedge io_clock) begin
    if(_zz_1_) begin
      logic_ram[logic_pushPtr_value] <= _zz_13_;
    end
  end

  `ifndef SYNTHESIS
  always @(*) begin
    case(io_push_payload_mode)
      `CmdMode_defaultEncoding_DATA : io_push_payload_mode_string = "DATA";
      `CmdMode_defaultEncoding_SS : io_push_payload_mode_string = "SS  ";
      default : io_push_payload_mode_string = "????";
    endcase
  end
  always @(*) begin
    case(io_pop_payload_mode)
      `CmdMode_defaultEncoding_DATA : io_pop_payload_mode_string = "DATA";
      `CmdMode_defaultEncoding_SS : io_pop_payload_mode_string = "SS  ";
      default : io_pop_payload_mode_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_3_)
      `CmdMode_defaultEncoding_DATA : _zz_3__string = "DATA";
      `CmdMode_defaultEncoding_SS : _zz_3__string = "SS  ";
      default : _zz_3__string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_5_)
      `CmdMode_defaultEncoding_DATA : _zz_5__string = "DATA";
      `CmdMode_defaultEncoding_SS : _zz_5__string = "SS  ";
      default : _zz_5__string = "????";
    endcase
  end
  `endif

  always @ (*) begin
    _zz_1_ = 1'b0;
    if(logic_pushing)begin
      _zz_1_ = 1'b1;
    end
  end

  always @ (*) begin
    logic_pushPtr_willIncrement = 1'b0;
    if(logic_pushing)begin
      logic_pushPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    logic_pushPtr_willClear = 1'b0;
    if(io_flush)begin
      logic_pushPtr_willClear = 1'b1;
    end
  end

  assign logic_pushPtr_willOverflowIfInc = (logic_pushPtr_value == (4'b1111));
  assign logic_pushPtr_willOverflow = (logic_pushPtr_willOverflowIfInc && logic_pushPtr_willIncrement);
  always @ (*) begin
    logic_pushPtr_valueNext = (logic_pushPtr_value + _zz_8_);
    if(logic_pushPtr_willClear)begin
      logic_pushPtr_valueNext = (4'b0000);
    end
  end

  always @ (*) begin
    logic_popPtr_willIncrement = 1'b0;
    if(logic_popping)begin
      logic_popPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    logic_popPtr_willClear = 1'b0;
    if(io_flush)begin
      logic_popPtr_willClear = 1'b1;
    end
  end

  assign logic_popPtr_willOverflowIfInc = (logic_popPtr_value == (4'b1111));
  assign logic_popPtr_willOverflow = (logic_popPtr_willOverflowIfInc && logic_popPtr_willIncrement);
  always @ (*) begin
    logic_popPtr_valueNext = (logic_popPtr_value + _zz_10_);
    if(logic_popPtr_willClear)begin
      logic_popPtr_valueNext = (4'b0000);
    end
  end

  assign logic_ptrMatch = (logic_pushPtr_value == logic_popPtr_value);
  assign logic_pushing = (io_push_valid && io_push_ready);
  assign logic_popping = (io_pop_valid && io_pop_ready);
  assign logic_empty = (logic_ptrMatch && (! logic_risingOccupancy));
  assign logic_full = (logic_ptrMatch && logic_risingOccupancy);
  assign io_push_ready = (! logic_full);
  assign io_pop_valid = ((! logic_empty) && (! (_zz_2_ && (! logic_full))));
  assign _zz_4_ = _zz_6_;
  assign _zz_5_ = _zz_4_[0 : 0];
  assign _zz_3_ = _zz_5_;
  assign io_pop_payload_mode = _zz_3_;
  assign io_pop_payload_args = _zz_4_[9 : 1];
  assign logic_ptrDif = (logic_pushPtr_value - logic_popPtr_value);
  assign io_occupancy = {(logic_risingOccupancy && logic_ptrMatch),logic_ptrDif};
  assign io_availability = {((! logic_risingOccupancy) && logic_ptrMatch),_zz_11_};
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      logic_pushPtr_value <= (4'b0000);
      logic_popPtr_value <= (4'b0000);
      logic_risingOccupancy <= 1'b0;
      _zz_2_ <= 1'b0;
    end else begin
      logic_pushPtr_value <= logic_pushPtr_valueNext;
      logic_popPtr_value <= logic_popPtr_valueNext;
      _zz_2_ <= (logic_popPtr_valueNext == logic_pushPtr_value);
      if((logic_pushing != logic_popping))begin
        logic_risingOccupancy <= logic_pushing;
      end
      if(io_flush)begin
        logic_risingOccupancy <= 1'b0;
      end
    end
  end


endmodule

module StreamFifo_7_ (
  input               io_push_valid,
  output              io_push_ready,
  input      [7:0]    io_push_payload,
  output              io_pop_valid,
  input               io_pop_ready,
  output     [7:0]    io_pop_payload,
  input               io_flush,
  output     [4:0]    io_occupancy,
  output     [4:0]    io_availability,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [7:0]    _zz_3_;
  wire       [0:0]    _zz_4_;
  wire       [3:0]    _zz_5_;
  wire       [0:0]    _zz_6_;
  wire       [3:0]    _zz_7_;
  wire       [3:0]    _zz_8_;
  wire                _zz_9_;
  reg                 _zz_1_;
  reg                 logic_pushPtr_willIncrement;
  reg                 logic_pushPtr_willClear;
  reg        [3:0]    logic_pushPtr_valueNext;
  reg        [3:0]    logic_pushPtr_value;
  wire                logic_pushPtr_willOverflowIfInc;
  wire                logic_pushPtr_willOverflow;
  reg                 logic_popPtr_willIncrement;
  reg                 logic_popPtr_willClear;
  reg        [3:0]    logic_popPtr_valueNext;
  reg        [3:0]    logic_popPtr_value;
  wire                logic_popPtr_willOverflowIfInc;
  wire                logic_popPtr_willOverflow;
  wire                logic_ptrMatch;
  reg                 logic_risingOccupancy;
  wire                logic_pushing;
  wire                logic_popping;
  wire                logic_empty;
  wire                logic_full;
  reg                 _zz_2_;
  wire       [3:0]    logic_ptrDif;
  reg [7:0] logic_ram [0:15];

  assign _zz_4_ = logic_pushPtr_willIncrement;
  assign _zz_5_ = {3'd0, _zz_4_};
  assign _zz_6_ = logic_popPtr_willIncrement;
  assign _zz_7_ = {3'd0, _zz_6_};
  assign _zz_8_ = (logic_popPtr_value - logic_pushPtr_value);
  assign _zz_9_ = 1'b1;
  always @ (posedge io_clock) begin
    if(_zz_9_) begin
      _zz_3_ <= logic_ram[logic_popPtr_valueNext];
    end
  end

  always @ (posedge io_clock) begin
    if(_zz_1_) begin
      logic_ram[logic_pushPtr_value] <= io_push_payload;
    end
  end

  always @ (*) begin
    _zz_1_ = 1'b0;
    if(logic_pushing)begin
      _zz_1_ = 1'b1;
    end
  end

  always @ (*) begin
    logic_pushPtr_willIncrement = 1'b0;
    if(logic_pushing)begin
      logic_pushPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    logic_pushPtr_willClear = 1'b0;
    if(io_flush)begin
      logic_pushPtr_willClear = 1'b1;
    end
  end

  assign logic_pushPtr_willOverflowIfInc = (logic_pushPtr_value == (4'b1111));
  assign logic_pushPtr_willOverflow = (logic_pushPtr_willOverflowIfInc && logic_pushPtr_willIncrement);
  always @ (*) begin
    logic_pushPtr_valueNext = (logic_pushPtr_value + _zz_5_);
    if(logic_pushPtr_willClear)begin
      logic_pushPtr_valueNext = (4'b0000);
    end
  end

  always @ (*) begin
    logic_popPtr_willIncrement = 1'b0;
    if(logic_popping)begin
      logic_popPtr_willIncrement = 1'b1;
    end
  end

  always @ (*) begin
    logic_popPtr_willClear = 1'b0;
    if(io_flush)begin
      logic_popPtr_willClear = 1'b1;
    end
  end

  assign logic_popPtr_willOverflowIfInc = (logic_popPtr_value == (4'b1111));
  assign logic_popPtr_willOverflow = (logic_popPtr_willOverflowIfInc && logic_popPtr_willIncrement);
  always @ (*) begin
    logic_popPtr_valueNext = (logic_popPtr_value + _zz_7_);
    if(logic_popPtr_willClear)begin
      logic_popPtr_valueNext = (4'b0000);
    end
  end

  assign logic_ptrMatch = (logic_pushPtr_value == logic_popPtr_value);
  assign logic_pushing = (io_push_valid && io_push_ready);
  assign logic_popping = (io_pop_valid && io_pop_ready);
  assign logic_empty = (logic_ptrMatch && (! logic_risingOccupancy));
  assign logic_full = (logic_ptrMatch && logic_risingOccupancy);
  assign io_push_ready = (! logic_full);
  assign io_pop_valid = ((! logic_empty) && (! (_zz_2_ && (! logic_full))));
  assign io_pop_payload = _zz_3_;
  assign logic_ptrDif = (logic_pushPtr_value - logic_popPtr_value);
  assign io_occupancy = {(logic_risingOccupancy && logic_ptrMatch),logic_ptrDif};
  assign io_availability = {((! logic_risingOccupancy) && logic_ptrMatch),_zz_8_};
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      logic_pushPtr_value <= (4'b0000);
      logic_popPtr_value <= (4'b0000);
      logic_risingOccupancy <= 1'b0;
      _zz_2_ <= 1'b0;
    end else begin
      logic_pushPtr_value <= logic_pushPtr_valueNext;
      logic_popPtr_value <= logic_popPtr_valueNext;
      _zz_2_ <= (logic_popPtr_valueNext == logic_pushPtr_value);
      if((logic_pushing != logic_popping))begin
        logic_risingOccupancy <= logic_pushing;
      end
      if(io_flush)begin
        logic_risingOccupancy <= 1'b0;
      end
    end
  end


endmodule
//InterruptCtrl_11_ replaced by InterruptCtrl

module UniqueIDCtrl (
);


endmodule

module BufferCC_8_ (
  input               io_dataIn,
  output              io_dataOut,
  input               io_clock 
);
  reg                 buffers_0;
  reg                 buffers_1;

  assign io_dataOut = buffers_1;
  always @ (posedge io_clock) begin
    buffers_0 <= io_dataIn;
    buffers_1 <= buffers_0;
  end


endmodule

module VexRiscv (
  output              iBus_cmd_valid,
  input               iBus_cmd_ready,
  output     [31:0]   iBus_cmd_payload_pc,
  input               iBus_rsp_valid,
  input               iBus_rsp_payload_error,
  input      [31:0]   iBus_rsp_payload_inst,
  input               timerInterrupt,
  input               externalInterrupt,
  input               softwareInterrupt,
  input               debug_bus_cmd_valid,
  output reg          debug_bus_cmd_ready,
  input               debug_bus_cmd_payload_wr,
  input      [7:0]    debug_bus_cmd_payload_address,
  input      [31:0]   debug_bus_cmd_payload_data,
  output reg [31:0]   debug_bus_rsp_data,
  output              debug_resetOut,
  output              dBus_cmd_valid,
  input               dBus_cmd_ready,
  output              dBus_cmd_payload_wr,
  output     [31:0]   dBus_cmd_payload_address,
  output     [31:0]   dBus_cmd_payload_data,
  output     [1:0]    dBus_cmd_payload_size,
  input               dBus_rsp_ready,
  input               dBus_rsp_error,
  input      [31:0]   dBus_rsp_data,
  input               io_clock,
  input               resetCtrl_systemReset,
  input               resetCtrl_debugReset 
);
  wire                _zz_145_;
  wire                _zz_146_;
  wire       [31:0]   _zz_147_;
  wire       [31:0]   _zz_148_;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_c_io_push_ready;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_error;
  wire       [31:0]   IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_inst;
  wire       [1:0]    IBusSimplePlugin_rspJoin_rspBuffer_c_io_occupancy;
  wire                _zz_149_;
  wire                _zz_150_;
  wire                _zz_151_;
  wire                _zz_152_;
  wire                _zz_153_;
  wire                _zz_154_;
  wire                _zz_155_;
  wire                _zz_156_;
  wire                _zz_157_;
  wire                _zz_158_;
  wire                _zz_159_;
  wire                _zz_160_;
  wire                _zz_161_;
  wire       [1:0]    _zz_162_;
  wire                _zz_163_;
  wire                _zz_164_;
  wire                _zz_165_;
  wire                _zz_166_;
  wire                _zz_167_;
  wire                _zz_168_;
  wire       [1:0]    _zz_169_;
  wire                _zz_170_;
  wire                _zz_171_;
  wire                _zz_172_;
  wire                _zz_173_;
  wire       [5:0]    _zz_174_;
  wire                _zz_175_;
  wire                _zz_176_;
  wire                _zz_177_;
  wire                _zz_178_;
  wire       [1:0]    _zz_179_;
  wire       [1:0]    _zz_180_;
  wire                _zz_181_;
  wire       [0:0]    _zz_182_;
  wire       [0:0]    _zz_183_;
  wire       [0:0]    _zz_184_;
  wire       [51:0]   _zz_185_;
  wire       [51:0]   _zz_186_;
  wire       [51:0]   _zz_187_;
  wire       [32:0]   _zz_188_;
  wire       [51:0]   _zz_189_;
  wire       [49:0]   _zz_190_;
  wire       [51:0]   _zz_191_;
  wire       [49:0]   _zz_192_;
  wire       [51:0]   _zz_193_;
  wire       [0:0]    _zz_194_;
  wire       [0:0]    _zz_195_;
  wire       [0:0]    _zz_196_;
  wire       [0:0]    _zz_197_;
  wire       [0:0]    _zz_198_;
  wire       [0:0]    _zz_199_;
  wire       [0:0]    _zz_200_;
  wire       [0:0]    _zz_201_;
  wire       [0:0]    _zz_202_;
  wire       [0:0]    _zz_203_;
  wire       [32:0]   _zz_204_;
  wire       [31:0]   _zz_205_;
  wire       [32:0]   _zz_206_;
  wire       [0:0]    _zz_207_;
  wire       [0:0]    _zz_208_;
  wire       [0:0]    _zz_209_;
  wire       [1:0]    _zz_210_;
  wire       [1:0]    _zz_211_;
  wire       [2:0]    _zz_212_;
  wire       [31:0]   _zz_213_;
  wire       [2:0]    _zz_214_;
  wire       [0:0]    _zz_215_;
  wire       [2:0]    _zz_216_;
  wire       [0:0]    _zz_217_;
  wire       [2:0]    _zz_218_;
  wire       [0:0]    _zz_219_;
  wire       [2:0]    _zz_220_;
  wire       [0:0]    _zz_221_;
  wire       [2:0]    _zz_222_;
  wire       [4:0]    _zz_223_;
  wire       [11:0]   _zz_224_;
  wire       [11:0]   _zz_225_;
  wire       [31:0]   _zz_226_;
  wire       [31:0]   _zz_227_;
  wire       [31:0]   _zz_228_;
  wire       [31:0]   _zz_229_;
  wire       [31:0]   _zz_230_;
  wire       [31:0]   _zz_231_;
  wire       [31:0]   _zz_232_;
  wire       [65:0]   _zz_233_;
  wire       [65:0]   _zz_234_;
  wire       [31:0]   _zz_235_;
  wire       [31:0]   _zz_236_;
  wire       [0:0]    _zz_237_;
  wire       [5:0]    _zz_238_;
  wire       [32:0]   _zz_239_;
  wire       [31:0]   _zz_240_;
  wire       [31:0]   _zz_241_;
  wire       [32:0]   _zz_242_;
  wire       [32:0]   _zz_243_;
  wire       [32:0]   _zz_244_;
  wire       [32:0]   _zz_245_;
  wire       [0:0]    _zz_246_;
  wire       [32:0]   _zz_247_;
  wire       [0:0]    _zz_248_;
  wire       [32:0]   _zz_249_;
  wire       [0:0]    _zz_250_;
  wire       [31:0]   _zz_251_;
  wire       [1:0]    _zz_252_;
  wire       [1:0]    _zz_253_;
  wire       [19:0]   _zz_254_;
  wire       [11:0]   _zz_255_;
  wire       [11:0]   _zz_256_;
  wire       [0:0]    _zz_257_;
  wire       [0:0]    _zz_258_;
  wire       [0:0]    _zz_259_;
  wire       [0:0]    _zz_260_;
  wire       [0:0]    _zz_261_;
  wire       [0:0]    _zz_262_;
  wire       [0:0]    _zz_263_;
  wire       [31:0]   _zz_264_;
  wire       [31:0]   _zz_265_;
  wire       [31:0]   _zz_266_;
  wire                _zz_267_;
  wire       [0:0]    _zz_268_;
  wire       [12:0]   _zz_269_;
  wire       [31:0]   _zz_270_;
  wire       [31:0]   _zz_271_;
  wire       [31:0]   _zz_272_;
  wire                _zz_273_;
  wire       [0:0]    _zz_274_;
  wire       [6:0]    _zz_275_;
  wire       [31:0]   _zz_276_;
  wire       [31:0]   _zz_277_;
  wire       [31:0]   _zz_278_;
  wire                _zz_279_;
  wire       [0:0]    _zz_280_;
  wire       [0:0]    _zz_281_;
  wire       [31:0]   _zz_282_;
  wire       [31:0]   _zz_283_;
  wire       [31:0]   _zz_284_;
  wire       [31:0]   _zz_285_;
  wire                _zz_286_;
  wire       [0:0]    _zz_287_;
  wire       [0:0]    _zz_288_;
  wire       [0:0]    _zz_289_;
  wire       [4:0]    _zz_290_;
  wire       [0:0]    _zz_291_;
  wire       [0:0]    _zz_292_;
  wire                _zz_293_;
  wire       [0:0]    _zz_294_;
  wire       [24:0]   _zz_295_;
  wire       [31:0]   _zz_296_;
  wire       [31:0]   _zz_297_;
  wire       [31:0]   _zz_298_;
  wire       [31:0]   _zz_299_;
  wire                _zz_300_;
  wire       [0:0]    _zz_301_;
  wire       [1:0]    _zz_302_;
  wire                _zz_303_;
  wire                _zz_304_;
  wire                _zz_305_;
  wire       [3:0]    _zz_306_;
  wire       [3:0]    _zz_307_;
  wire                _zz_308_;
  wire       [0:0]    _zz_309_;
  wire       [21:0]   _zz_310_;
  wire       [31:0]   _zz_311_;
  wire       [31:0]   _zz_312_;
  wire       [31:0]   _zz_313_;
  wire                _zz_314_;
  wire                _zz_315_;
  wire       [31:0]   _zz_316_;
  wire       [31:0]   _zz_317_;
  wire       [31:0]   _zz_318_;
  wire                _zz_319_;
  wire       [0:0]    _zz_320_;
  wire       [1:0]    _zz_321_;
  wire                _zz_322_;
  wire       [1:0]    _zz_323_;
  wire       [1:0]    _zz_324_;
  wire                _zz_325_;
  wire       [0:0]    _zz_326_;
  wire       [19:0]   _zz_327_;
  wire       [31:0]   _zz_328_;
  wire       [31:0]   _zz_329_;
  wire       [31:0]   _zz_330_;
  wire       [31:0]   _zz_331_;
  wire       [31:0]   _zz_332_;
  wire                _zz_333_;
  wire       [31:0]   _zz_334_;
  wire                _zz_335_;
  wire                _zz_336_;
  wire                _zz_337_;
  wire       [0:0]    _zz_338_;
  wire       [0:0]    _zz_339_;
  wire                _zz_340_;
  wire       [0:0]    _zz_341_;
  wire       [17:0]   _zz_342_;
  wire       [31:0]   _zz_343_;
  wire       [31:0]   _zz_344_;
  wire       [0:0]    _zz_345_;
  wire       [0:0]    _zz_346_;
  wire       [1:0]    _zz_347_;
  wire       [1:0]    _zz_348_;
  wire                _zz_349_;
  wire       [0:0]    _zz_350_;
  wire       [14:0]   _zz_351_;
  wire       [31:0]   _zz_352_;
  wire       [31:0]   _zz_353_;
  wire       [31:0]   _zz_354_;
  wire       [0:0]    _zz_355_;
  wire       [0:0]    _zz_356_;
  wire                _zz_357_;
  wire       [0:0]    _zz_358_;
  wire       [0:0]    _zz_359_;
  wire                _zz_360_;
  wire       [0:0]    _zz_361_;
  wire       [11:0]   _zz_362_;
  wire       [31:0]   _zz_363_;
  wire       [31:0]   _zz_364_;
  wire       [31:0]   _zz_365_;
  wire       [31:0]   _zz_366_;
  wire                _zz_367_;
  wire       [1:0]    _zz_368_;
  wire       [1:0]    _zz_369_;
  wire                _zz_370_;
  wire       [0:0]    _zz_371_;
  wire       [8:0]    _zz_372_;
  wire       [31:0]   _zz_373_;
  wire       [31:0]   _zz_374_;
  wire       [31:0]   _zz_375_;
  wire                _zz_376_;
  wire       [0:0]    _zz_377_;
  wire       [0:0]    _zz_378_;
  wire       [0:0]    _zz_379_;
  wire       [0:0]    _zz_380_;
  wire       [0:0]    _zz_381_;
  wire       [0:0]    _zz_382_;
  wire                _zz_383_;
  wire       [0:0]    _zz_384_;
  wire       [4:0]    _zz_385_;
  wire       [31:0]   _zz_386_;
  wire       [31:0]   _zz_387_;
  wire       [31:0]   _zz_388_;
  wire       [31:0]   _zz_389_;
  wire                _zz_390_;
  wire                _zz_391_;
  wire                _zz_392_;
  wire       [1:0]    _zz_393_;
  wire       [1:0]    _zz_394_;
  wire                _zz_395_;
  wire       [0:0]    _zz_396_;
  wire       [1:0]    _zz_397_;
  wire       [31:0]   _zz_398_;
  wire       [31:0]   _zz_399_;
  wire       [31:0]   _zz_400_;
  wire       [31:0]   _zz_401_;
  wire                _zz_402_;
  wire       [3:0]    _zz_403_;
  wire       [3:0]    _zz_404_;
  wire       [4:0]    _zz_405_;
  wire       [4:0]    _zz_406_;
  wire       [31:0]   _zz_407_;
  wire       [31:0]   _zz_408_;
  wire       [31:0]   _zz_409_;
  wire                _zz_410_;
  wire                _zz_411_;
  wire       [31:0]   _zz_412_;
  wire                _zz_413_;
  wire       [0:0]    _zz_414_;
  wire       [0:0]    _zz_415_;
  wire       [31:0]   _zz_416_;
  wire       [31:0]   _zz_417_;
  wire       [31:0]   _zz_418_;
  wire       [31:0]   _zz_419_;
  wire       `AluBitwiseCtrlEnum_defaultEncoding_type decode_ALU_BITWISE_CTRL;
  wire       `AluBitwiseCtrlEnum_defaultEncoding_type _zz_1_;
  wire       `AluBitwiseCtrlEnum_defaultEncoding_type _zz_2_;
  wire       `AluBitwiseCtrlEnum_defaultEncoding_type _zz_3_;
  wire       `Src2CtrlEnum_defaultEncoding_type decode_SRC2_CTRL;
  wire       `Src2CtrlEnum_defaultEncoding_type _zz_4_;
  wire       `Src2CtrlEnum_defaultEncoding_type _zz_5_;
  wire       `Src2CtrlEnum_defaultEncoding_type _zz_6_;
  wire       [31:0]   memory_PC;
  wire                memory_IS_MUL;
  wire                execute_IS_MUL;
  wire                decode_IS_MUL;
  wire       [33:0]   memory_MUL_HH;
  wire       [33:0]   execute_MUL_HH;
  wire                execute_BYPASSABLE_MEMORY_STAGE;
  wire                decode_BYPASSABLE_MEMORY_STAGE;
  wire                decode_CSR_WRITE_OPCODE;
  wire                decode_SRC_LESS_UNSIGNED;
  wire       [51:0]   memory_MUL_LOW;
  wire       [31:0]   writeBack_FORMAL_PC_NEXT;
  wire       [31:0]   memory_FORMAL_PC_NEXT;
  wire       [31:0]   execute_FORMAL_PC_NEXT;
  wire       [31:0]   decode_FORMAL_PC_NEXT;
  wire                decode_IS_DIV;
  wire                decode_IS_CSR;
  wire       [33:0]   execute_MUL_LH;
  wire                decode_MEMORY_STORE;
  wire                decode_SRC2_FORCE_ZERO;
  wire                decode_DO_EBREAK;
  wire       [1:0]    memory_MEMORY_ADDRESS_LOW;
  wire       [1:0]    execute_MEMORY_ADDRESS_LOW;
  wire       [31:0]   memory_MEMORY_READ_DATA;
  wire       `AluCtrlEnum_defaultEncoding_type decode_ALU_CTRL;
  wire       `AluCtrlEnum_defaultEncoding_type _zz_7_;
  wire       `AluCtrlEnum_defaultEncoding_type _zz_8_;
  wire       `AluCtrlEnum_defaultEncoding_type _zz_9_;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_10_;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_11_;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_12_;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_13_;
  wire       `EnvCtrlEnum_defaultEncoding_type decode_ENV_CTRL;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_14_;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_15_;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_16_;
  wire                decode_MEMORY_ENABLE;
  wire                decode_IS_RS1_SIGNED;
  wire       [33:0]   execute_MUL_HL;
  wire                decode_BYPASSABLE_EXECUTE_STAGE;
  wire       `Src1CtrlEnum_defaultEncoding_type decode_SRC1_CTRL;
  wire       `Src1CtrlEnum_defaultEncoding_type _zz_17_;
  wire       `Src1CtrlEnum_defaultEncoding_type _zz_18_;
  wire       `Src1CtrlEnum_defaultEncoding_type _zz_19_;
  wire                decode_CSR_READ_OPCODE;
  wire                decode_IS_RS2_SIGNED;
  wire       `BranchCtrlEnum_defaultEncoding_type decode_BRANCH_CTRL;
  wire       `BranchCtrlEnum_defaultEncoding_type _zz_20_;
  wire       `BranchCtrlEnum_defaultEncoding_type _zz_21_;
  wire       `BranchCtrlEnum_defaultEncoding_type _zz_22_;
  wire       [31:0]   execute_MUL_LL;
  wire       `ShiftCtrlEnum_defaultEncoding_type decode_SHIFT_CTRL;
  wire       `ShiftCtrlEnum_defaultEncoding_type _zz_23_;
  wire       `ShiftCtrlEnum_defaultEncoding_type _zz_24_;
  wire       `ShiftCtrlEnum_defaultEncoding_type _zz_25_;
  wire       [31:0]   writeBack_REGFILE_WRITE_DATA;
  wire       [31:0]   memory_REGFILE_WRITE_DATA;
  wire       [31:0]   execute_REGFILE_WRITE_DATA;
  wire                execute_DO_EBREAK;
  wire                decode_IS_EBREAK;
  wire       [31:0]   execute_BRANCH_CALC;
  wire                execute_BRANCH_DO;
  wire       [31:0]   execute_PC;
  wire       `BranchCtrlEnum_defaultEncoding_type execute_BRANCH_CTRL;
  wire       `BranchCtrlEnum_defaultEncoding_type _zz_26_;
  wire                execute_CSR_READ_OPCODE;
  wire                execute_CSR_WRITE_OPCODE;
  wire                execute_IS_CSR;
  wire       `EnvCtrlEnum_defaultEncoding_type memory_ENV_CTRL;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_27_;
  wire       `EnvCtrlEnum_defaultEncoding_type execute_ENV_CTRL;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_28_;
  wire       `EnvCtrlEnum_defaultEncoding_type writeBack_ENV_CTRL;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_29_;
  wire                execute_IS_RS1_SIGNED;
  wire                execute_IS_DIV;
  wire                execute_IS_RS2_SIGNED;
  wire                memory_IS_DIV;
  wire                writeBack_IS_MUL;
  wire       [33:0]   writeBack_MUL_HH;
  wire       [51:0]   writeBack_MUL_LOW;
  wire       [33:0]   memory_MUL_HL;
  wire       [33:0]   memory_MUL_LH;
  wire       [31:0]   memory_MUL_LL;
  (* keep , syn_keep *) wire       [31:0]   execute_RS1 /* synthesis syn_keep = 1 */ ;
  wire                decode_RS2_USE;
  wire                decode_RS1_USE;
  wire                execute_REGFILE_WRITE_VALID;
  wire                execute_BYPASSABLE_EXECUTE_STAGE;
  reg        [31:0]   _zz_30_;
  wire                memory_REGFILE_WRITE_VALID;
  wire       [31:0]   memory_INSTRUCTION;
  wire                memory_BYPASSABLE_MEMORY_STAGE;
  wire                writeBack_REGFILE_WRITE_VALID;
  reg        [31:0]   decode_RS2;
  reg        [31:0]   decode_RS1;
  wire       [31:0]   execute_SHIFT_RIGHT;
  reg        [31:0]   _zz_31_;
  wire       `ShiftCtrlEnum_defaultEncoding_type execute_SHIFT_CTRL;
  wire       `ShiftCtrlEnum_defaultEncoding_type _zz_32_;
  wire                execute_SRC_LESS_UNSIGNED;
  wire                execute_SRC2_FORCE_ZERO;
  wire                execute_SRC_USE_SUB_LESS;
  wire       [31:0]   _zz_33_;
  wire       `Src2CtrlEnum_defaultEncoding_type execute_SRC2_CTRL;
  wire       `Src2CtrlEnum_defaultEncoding_type _zz_34_;
  wire       `Src1CtrlEnum_defaultEncoding_type execute_SRC1_CTRL;
  wire       `Src1CtrlEnum_defaultEncoding_type _zz_35_;
  wire                decode_SRC_USE_SUB_LESS;
  wire                decode_SRC_ADD_ZERO;
  wire       [31:0]   execute_SRC_ADD_SUB;
  wire                execute_SRC_LESS;
  wire       `AluCtrlEnum_defaultEncoding_type execute_ALU_CTRL;
  wire       `AluCtrlEnum_defaultEncoding_type _zz_36_;
  wire       [31:0]   execute_SRC2;
  wire       [31:0]   execute_SRC1;
  wire       `AluBitwiseCtrlEnum_defaultEncoding_type execute_ALU_BITWISE_CTRL;
  wire       `AluBitwiseCtrlEnum_defaultEncoding_type _zz_37_;
  wire       [31:0]   _zz_38_;
  wire                _zz_39_;
  reg                 _zz_40_;
  reg                 decode_REGFILE_WRITE_VALID;
  wire                decode_LEGAL_INSTRUCTION;
  wire       `ShiftCtrlEnum_defaultEncoding_type _zz_41_;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_42_;
  wire       `AluCtrlEnum_defaultEncoding_type _zz_43_;
  wire       `Src2CtrlEnum_defaultEncoding_type _zz_44_;
  wire       `AluBitwiseCtrlEnum_defaultEncoding_type _zz_45_;
  wire       `Src1CtrlEnum_defaultEncoding_type _zz_46_;
  wire       `BranchCtrlEnum_defaultEncoding_type _zz_47_;
  wire                writeBack_MEMORY_STORE;
  reg        [31:0]   _zz_48_;
  wire                writeBack_MEMORY_ENABLE;
  wire       [1:0]    writeBack_MEMORY_ADDRESS_LOW;
  wire       [31:0]   writeBack_MEMORY_READ_DATA;
  wire                memory_MEMORY_STORE;
  wire                memory_MEMORY_ENABLE;
  wire       [31:0]   execute_SRC_ADD;
  (* keep , syn_keep *) wire       [31:0]   execute_RS2 /* synthesis syn_keep = 1 */ ;
  wire       [31:0]   execute_INSTRUCTION;
  wire                execute_MEMORY_STORE;
  wire                execute_MEMORY_ENABLE;
  wire                execute_ALIGNEMENT_FAULT;
  reg        [31:0]   _zz_49_;
  wire       [31:0]   decode_PC;
  wire       [31:0]   decode_INSTRUCTION;
  wire       [31:0]   writeBack_PC;
  wire       [31:0]   writeBack_INSTRUCTION;
  reg                 decode_arbitration_haltItself;
  reg                 decode_arbitration_haltByOther;
  reg                 decode_arbitration_removeIt;
  wire                decode_arbitration_flushIt;
  reg                 decode_arbitration_flushNext;
  reg                 decode_arbitration_isValid;
  wire                decode_arbitration_isStuck;
  wire                decode_arbitration_isStuckByOthers;
  wire                decode_arbitration_isFlushed;
  wire                decode_arbitration_isMoving;
  wire                decode_arbitration_isFiring;
  reg                 execute_arbitration_haltItself;
  reg                 execute_arbitration_haltByOther;
  reg                 execute_arbitration_removeIt;
  reg                 execute_arbitration_flushIt;
  reg                 execute_arbitration_flushNext;
  reg                 execute_arbitration_isValid;
  wire                execute_arbitration_isStuck;
  wire                execute_arbitration_isStuckByOthers;
  wire                execute_arbitration_isFlushed;
  wire                execute_arbitration_isMoving;
  wire                execute_arbitration_isFiring;
  reg                 memory_arbitration_haltItself;
  wire                memory_arbitration_haltByOther;
  reg                 memory_arbitration_removeIt;
  wire                memory_arbitration_flushIt;
  wire                memory_arbitration_flushNext;
  reg                 memory_arbitration_isValid;
  wire                memory_arbitration_isStuck;
  wire                memory_arbitration_isStuckByOthers;
  wire                memory_arbitration_isFlushed;
  wire                memory_arbitration_isMoving;
  wire                memory_arbitration_isFiring;
  wire                writeBack_arbitration_haltItself;
  wire                writeBack_arbitration_haltByOther;
  reg                 writeBack_arbitration_removeIt;
  wire                writeBack_arbitration_flushIt;
  reg                 writeBack_arbitration_flushNext;
  reg                 writeBack_arbitration_isValid;
  wire                writeBack_arbitration_isStuck;
  wire                writeBack_arbitration_isStuckByOthers;
  wire                writeBack_arbitration_isFlushed;
  wire                writeBack_arbitration_isMoving;
  wire                writeBack_arbitration_isFiring;
  wire       [31:0]   lastStageInstruction /* verilator public */ ;
  wire       [31:0]   lastStagePc /* verilator public */ ;
  wire                lastStageIsValid /* verilator public */ ;
  wire                lastStageIsFiring /* verilator public */ ;
  reg                 IBusSimplePlugin_fetcherHalt;
  reg                 IBusSimplePlugin_incomingInstruction;
  wire                IBusSimplePlugin_pcValids_0;
  wire                IBusSimplePlugin_pcValids_1;
  wire                IBusSimplePlugin_pcValids_2;
  wire                IBusSimplePlugin_pcValids_3;
  wire                decodeExceptionPort_valid;
  wire       [3:0]    decodeExceptionPort_payload_code;
  wire       [31:0]   decodeExceptionPort_payload_badAddr;
  reg                 CsrPlugin_inWfi /* verilator public */ ;
  reg                 CsrPlugin_thirdPartyWake;
  reg                 CsrPlugin_jumpInterface_valid;
  reg        [31:0]   CsrPlugin_jumpInterface_payload;
  wire                CsrPlugin_exceptionPendings_0;
  wire                CsrPlugin_exceptionPendings_1;
  wire                CsrPlugin_exceptionPendings_2;
  wire                CsrPlugin_exceptionPendings_3;
  wire                contextSwitching;
  reg        [1:0]    CsrPlugin_privilege;
  reg                 CsrPlugin_forceMachineWire;
  reg                 CsrPlugin_selfException_valid;
  reg        [3:0]    CsrPlugin_selfException_payload_code;
  wire       [31:0]   CsrPlugin_selfException_payload_badAddr;
  reg                 CsrPlugin_allowInterrupts;
  reg                 CsrPlugin_allowException;
  wire                BranchPlugin_jumpInterface_valid;
  wire       [31:0]   BranchPlugin_jumpInterface_payload;
  reg                 BranchPlugin_branchExceptionPort_valid;
  wire       [3:0]    BranchPlugin_branchExceptionPort_payload_code;
  wire       [31:0]   BranchPlugin_branchExceptionPort_payload_badAddr;
  reg                 IBusSimplePlugin_injectionPort_valid;
  reg                 IBusSimplePlugin_injectionPort_ready;
  wire       [31:0]   IBusSimplePlugin_injectionPort_payload;
  wire                IBusSimplePlugin_externalFlush;
  wire                IBusSimplePlugin_jump_pcLoad_valid;
  wire       [31:0]   IBusSimplePlugin_jump_pcLoad_payload;
  wire       [1:0]    _zz_50_;
  wire                IBusSimplePlugin_fetchPc_output_valid;
  wire                IBusSimplePlugin_fetchPc_output_ready;
  wire       [31:0]   IBusSimplePlugin_fetchPc_output_payload;
  reg        [31:0]   IBusSimplePlugin_fetchPc_pcReg /* verilator public */ ;
  reg                 IBusSimplePlugin_fetchPc_correction;
  reg                 IBusSimplePlugin_fetchPc_correctionReg;
  wire                IBusSimplePlugin_fetchPc_corrected;
  wire                IBusSimplePlugin_fetchPc_pcRegPropagate;
  reg                 IBusSimplePlugin_fetchPc_booted;
  reg                 IBusSimplePlugin_fetchPc_inc;
  reg        [31:0]   IBusSimplePlugin_fetchPc_pc;
  reg                 IBusSimplePlugin_fetchPc_flushed;
  wire                IBusSimplePlugin_iBusRsp_redoFetch;
  wire                IBusSimplePlugin_iBusRsp_stages_0_input_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_0_input_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_0_input_payload;
  wire                IBusSimplePlugin_iBusRsp_stages_0_output_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_0_output_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_0_output_payload;
  wire                IBusSimplePlugin_iBusRsp_stages_0_halt;
  wire                IBusSimplePlugin_iBusRsp_stages_1_input_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_1_input_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_1_input_payload;
  wire                IBusSimplePlugin_iBusRsp_stages_1_output_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_1_output_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_1_output_payload;
  reg                 IBusSimplePlugin_iBusRsp_stages_1_halt;
  wire                IBusSimplePlugin_iBusRsp_stages_2_input_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_2_input_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_2_input_payload;
  wire                IBusSimplePlugin_iBusRsp_stages_2_output_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_2_output_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_2_output_payload;
  wire                IBusSimplePlugin_iBusRsp_stages_2_halt;
  wire                _zz_51_;
  wire                _zz_52_;
  wire                _zz_53_;
  wire                IBusSimplePlugin_iBusRsp_flush;
  wire                _zz_54_;
  reg                 _zz_55_;
  reg        [31:0]   _zz_56_;
  wire                _zz_57_;
  reg                 _zz_58_;
  reg        [31:0]   _zz_59_;
  reg                 IBusSimplePlugin_iBusRsp_readyForError;
  wire                IBusSimplePlugin_iBusRsp_output_valid;
  wire                IBusSimplePlugin_iBusRsp_output_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_output_payload_pc;
  wire                IBusSimplePlugin_iBusRsp_output_payload_rsp_error;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_output_payload_rsp_inst;
  wire                IBusSimplePlugin_iBusRsp_output_payload_isRvc;
  wire                IBusSimplePlugin_injector_decodeInput_valid;
  wire                IBusSimplePlugin_injector_decodeInput_ready;
  wire       [31:0]   IBusSimplePlugin_injector_decodeInput_payload_pc;
  wire                IBusSimplePlugin_injector_decodeInput_payload_rsp_error;
  wire       [31:0]   IBusSimplePlugin_injector_decodeInput_payload_rsp_inst;
  wire                IBusSimplePlugin_injector_decodeInput_payload_isRvc;
  reg                 _zz_60_;
  reg        [31:0]   _zz_61_;
  reg                 _zz_62_;
  reg        [31:0]   _zz_63_;
  reg                 _zz_64_;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_0;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_1;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_2;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_3;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_4;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_5;
  reg        [31:0]   IBusSimplePlugin_injector_formal_rawInDecode;
  wire                IBusSimplePlugin_cmd_valid;
  wire                IBusSimplePlugin_cmd_ready;
  wire       [31:0]   IBusSimplePlugin_cmd_payload_pc;
  wire                IBusSimplePlugin_pending_inc;
  wire                IBusSimplePlugin_pending_dec;
  reg        [2:0]    IBusSimplePlugin_pending_value;
  wire       [2:0]    IBusSimplePlugin_pending_next;
  wire                IBusSimplePlugin_cmdFork_pendingFull;
  wire                IBusSimplePlugin_cmdFork_enterTheMarket;
  reg                 IBusSimplePlugin_cmdFork_cmdKeep;
  reg                 IBusSimplePlugin_cmdFork_cmdFired;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_output_valid;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_output_ready;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_output_payload_error;
  wire       [31:0]   IBusSimplePlugin_rspJoin_rspBuffer_output_payload_inst;
  reg        [2:0]    IBusSimplePlugin_rspJoin_rspBuffer_discardCounter;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_flush;
  wire       [31:0]   IBusSimplePlugin_rspJoin_fetchRsp_pc;
  reg                 IBusSimplePlugin_rspJoin_fetchRsp_rsp_error;
  wire       [31:0]   IBusSimplePlugin_rspJoin_fetchRsp_rsp_inst;
  wire                IBusSimplePlugin_rspJoin_fetchRsp_isRvc;
  wire                IBusSimplePlugin_rspJoin_join_valid;
  wire                IBusSimplePlugin_rspJoin_join_ready;
  wire       [31:0]   IBusSimplePlugin_rspJoin_join_payload_pc;
  wire                IBusSimplePlugin_rspJoin_join_payload_rsp_error;
  wire       [31:0]   IBusSimplePlugin_rspJoin_join_payload_rsp_inst;
  wire                IBusSimplePlugin_rspJoin_join_payload_isRvc;
  wire                IBusSimplePlugin_rspJoin_exceptionDetected;
  wire                _zz_65_;
  wire                _zz_66_;
  reg                 execute_DBusSimplePlugin_skipCmd;
  reg        [31:0]   _zz_67_;
  reg        [3:0]    _zz_68_;
  wire       [3:0]    execute_DBusSimplePlugin_formalMask;
  reg        [31:0]   writeBack_DBusSimplePlugin_rspShifted;
  wire                _zz_69_;
  reg        [31:0]   _zz_70_;
  wire                _zz_71_;
  reg        [31:0]   _zz_72_;
  reg        [31:0]   writeBack_DBusSimplePlugin_rspFormated;
  wire       [30:0]   _zz_73_;
  wire                _zz_74_;
  wire                _zz_75_;
  wire                _zz_76_;
  wire                _zz_77_;
  wire                _zz_78_;
  wire                _zz_79_;
  wire       `BranchCtrlEnum_defaultEncoding_type _zz_80_;
  wire       `Src1CtrlEnum_defaultEncoding_type _zz_81_;
  wire       `AluBitwiseCtrlEnum_defaultEncoding_type _zz_82_;
  wire       `Src2CtrlEnum_defaultEncoding_type _zz_83_;
  wire       `AluCtrlEnum_defaultEncoding_type _zz_84_;
  wire       `EnvCtrlEnum_defaultEncoding_type _zz_85_;
  wire       `ShiftCtrlEnum_defaultEncoding_type _zz_86_;
  wire       [4:0]    decode_RegFilePlugin_regFileReadAddress1;
  wire       [4:0]    decode_RegFilePlugin_regFileReadAddress2;
  wire       [31:0]   decode_RegFilePlugin_rs1Data;
  wire       [31:0]   decode_RegFilePlugin_rs2Data;
  reg                 lastStageRegFileWrite_valid /* verilator public */ ;
  wire       [4:0]    lastStageRegFileWrite_payload_address /* verilator public */ ;
  wire       [31:0]   lastStageRegFileWrite_payload_data /* verilator public */ ;
  reg                 _zz_87_;
  reg        [31:0]   execute_IntAluPlugin_bitwise;
  reg        [31:0]   _zz_88_;
  reg        [31:0]   _zz_89_;
  wire                _zz_90_;
  reg        [19:0]   _zz_91_;
  wire                _zz_92_;
  reg        [19:0]   _zz_93_;
  reg        [31:0]   _zz_94_;
  reg        [31:0]   execute_SrcPlugin_addSub;
  wire                execute_SrcPlugin_less;
  wire       [4:0]    execute_FullBarrelShifterPlugin_amplitude;
  reg        [31:0]   _zz_95_;
  wire       [31:0]   execute_FullBarrelShifterPlugin_reversed;
  reg        [31:0]   _zz_96_;
  reg                 _zz_97_;
  reg                 _zz_98_;
  reg                 _zz_99_;
  reg        [4:0]    _zz_100_;
  reg        [31:0]   _zz_101_;
  wire                _zz_102_;
  wire                _zz_103_;
  wire                _zz_104_;
  wire                _zz_105_;
  wire                _zz_106_;
  wire                _zz_107_;
  reg                 execute_MulPlugin_aSigned;
  reg                 execute_MulPlugin_bSigned;
  wire       [31:0]   execute_MulPlugin_a;
  wire       [31:0]   execute_MulPlugin_b;
  wire       [15:0]   execute_MulPlugin_aULow;
  wire       [15:0]   execute_MulPlugin_bULow;
  wire       [16:0]   execute_MulPlugin_aSLow;
  wire       [16:0]   execute_MulPlugin_bSLow;
  wire       [16:0]   execute_MulPlugin_aHigh;
  wire       [16:0]   execute_MulPlugin_bHigh;
  wire       [65:0]   writeBack_MulPlugin_result;
  reg        [32:0]   memory_DivPlugin_rs1;
  reg        [31:0]   memory_DivPlugin_rs2;
  reg        [64:0]   memory_DivPlugin_accumulator;
  wire                memory_DivPlugin_frontendOk;
  reg                 memory_DivPlugin_div_needRevert;
  reg                 memory_DivPlugin_div_counter_willIncrement;
  reg                 memory_DivPlugin_div_counter_willClear;
  reg        [5:0]    memory_DivPlugin_div_counter_valueNext;
  reg        [5:0]    memory_DivPlugin_div_counter_value;
  wire                memory_DivPlugin_div_counter_willOverflowIfInc;
  wire                memory_DivPlugin_div_counter_willOverflow;
  reg                 memory_DivPlugin_div_done;
  reg        [31:0]   memory_DivPlugin_div_result;
  wire       [31:0]   _zz_108_;
  wire       [32:0]   memory_DivPlugin_div_stage_0_remainderShifted;
  wire       [32:0]   memory_DivPlugin_div_stage_0_remainderMinusDenominator;
  wire       [31:0]   memory_DivPlugin_div_stage_0_outRemainder;
  wire       [31:0]   memory_DivPlugin_div_stage_0_outNumerator;
  wire       [31:0]   _zz_109_;
  wire                _zz_110_;
  wire                _zz_111_;
  reg        [32:0]   _zz_112_;
  reg        [1:0]    CsrPlugin_misa_base;
  reg        [25:0]   CsrPlugin_misa_extensions;
  reg        [1:0]    CsrPlugin_mtvec_mode;
  reg        [29:0]   CsrPlugin_mtvec_base;
  reg        [31:0]   CsrPlugin_mepc;
  reg                 CsrPlugin_mstatus_MIE;
  reg                 CsrPlugin_mstatus_MPIE;
  reg        [1:0]    CsrPlugin_mstatus_MPP;
  reg                 CsrPlugin_mip_MEIP;
  reg                 CsrPlugin_mip_MTIP;
  reg                 CsrPlugin_mip_MSIP;
  reg                 CsrPlugin_mie_MEIE;
  reg                 CsrPlugin_mie_MTIE;
  reg                 CsrPlugin_mie_MSIE;
  reg        [31:0]   CsrPlugin_mscratch;
  reg                 CsrPlugin_mcause_interrupt;
  reg        [3:0]    CsrPlugin_mcause_exceptionCode;
  reg        [31:0]   CsrPlugin_mtval;
  reg        [63:0]   CsrPlugin_mcycle = 64'b0000000000000000000000000000000000000000000000000000000000000000;
  reg        [63:0]   CsrPlugin_minstret = 64'b0000000000000000000000000000000000000000000000000000000000000000;
  wire                _zz_113_;
  wire                _zz_114_;
  wire                _zz_115_;
  reg                 CsrPlugin_exceptionPortCtrl_exceptionValids_decode;
  reg                 CsrPlugin_exceptionPortCtrl_exceptionValids_execute;
  reg                 CsrPlugin_exceptionPortCtrl_exceptionValids_memory;
  reg                 CsrPlugin_exceptionPortCtrl_exceptionValids_writeBack;
  reg                 CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_decode;
  reg                 CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_execute;
  reg                 CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_memory;
  reg                 CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_writeBack;
  reg        [3:0]    CsrPlugin_exceptionPortCtrl_exceptionContext_code;
  reg        [31:0]   CsrPlugin_exceptionPortCtrl_exceptionContext_badAddr;
  wire       [1:0]    CsrPlugin_exceptionPortCtrl_exceptionTargetPrivilegeUncapped;
  wire       [1:0]    CsrPlugin_exceptionPortCtrl_exceptionTargetPrivilege;
  wire       [1:0]    _zz_116_;
  wire                _zz_117_;
  reg                 CsrPlugin_interrupt_valid;
  reg        [3:0]    CsrPlugin_interrupt_code /* verilator public */ ;
  reg        [1:0]    CsrPlugin_interrupt_targetPrivilege;
  wire                CsrPlugin_exception;
  reg                 CsrPlugin_lastStageWasWfi;
  reg                 CsrPlugin_pipelineLiberator_pcValids_0;
  reg                 CsrPlugin_pipelineLiberator_pcValids_1;
  reg                 CsrPlugin_pipelineLiberator_pcValids_2;
  wire                CsrPlugin_pipelineLiberator_active;
  reg                 CsrPlugin_pipelineLiberator_done;
  wire                CsrPlugin_interruptJump /* verilator public */ ;
  reg                 CsrPlugin_hadException;
  reg        [1:0]    CsrPlugin_targetPrivilege;
  reg        [3:0]    CsrPlugin_trapCause;
  reg        [1:0]    CsrPlugin_xtvec_mode;
  reg        [29:0]   CsrPlugin_xtvec_base;
  reg                 execute_CsrPlugin_wfiWake;
  wire                execute_CsrPlugin_blockedBySideEffects;
  reg                 execute_CsrPlugin_illegalAccess;
  reg                 execute_CsrPlugin_illegalInstruction;
  wire       [31:0]   execute_CsrPlugin_readData;
  wire                execute_CsrPlugin_writeInstruction;
  wire                execute_CsrPlugin_readInstruction;
  wire                execute_CsrPlugin_writeEnable;
  wire                execute_CsrPlugin_readEnable;
  wire       [31:0]   execute_CsrPlugin_readToWriteData;
  reg        [31:0]   execute_CsrPlugin_writeData;
  wire       [11:0]   execute_CsrPlugin_csrAddress;
  wire                execute_BranchPlugin_eq;
  wire       [2:0]    _zz_118_;
  reg                 _zz_119_;
  reg                 _zz_120_;
  wire       [31:0]   execute_BranchPlugin_branch_src1;
  wire                _zz_121_;
  reg        [10:0]   _zz_122_;
  wire                _zz_123_;
  reg        [19:0]   _zz_124_;
  wire                _zz_125_;
  reg        [18:0]   _zz_126_;
  reg        [31:0]   _zz_127_;
  wire       [31:0]   execute_BranchPlugin_branch_src2;
  wire       [31:0]   execute_BranchPlugin_branchAdder;
  reg                 DebugPlugin_firstCycle;
  reg                 DebugPlugin_secondCycle;
  reg                 DebugPlugin_resetIt;
  reg                 DebugPlugin_haltIt;
  reg                 DebugPlugin_stepIt;
  reg                 DebugPlugin_isPipBusy;
  reg                 DebugPlugin_godmode;
  reg                 DebugPlugin_haltedByBreak;
  reg        [31:0]   DebugPlugin_busReadDataReg;
  reg                 _zz_128_;
  reg                 DebugPlugin_resetIt_regNext;
  reg        [31:0]   decode_to_execute_RS1;
  reg        [31:0]   execute_to_memory_REGFILE_WRITE_DATA;
  reg        [31:0]   memory_to_writeBack_REGFILE_WRITE_DATA;
  reg        `ShiftCtrlEnum_defaultEncoding_type decode_to_execute_SHIFT_CTRL;
  reg        [31:0]   execute_to_memory_MUL_LL;
  reg        `BranchCtrlEnum_defaultEncoding_type decode_to_execute_BRANCH_CTRL;
  reg                 decode_to_execute_IS_RS2_SIGNED;
  reg                 decode_to_execute_CSR_READ_OPCODE;
  reg        `Src1CtrlEnum_defaultEncoding_type decode_to_execute_SRC1_CTRL;
  reg                 decode_to_execute_BYPASSABLE_EXECUTE_STAGE;
  reg        [33:0]   execute_to_memory_MUL_HL;
  reg                 decode_to_execute_IS_RS1_SIGNED;
  reg                 decode_to_execute_MEMORY_ENABLE;
  reg                 execute_to_memory_MEMORY_ENABLE;
  reg                 memory_to_writeBack_MEMORY_ENABLE;
  reg        `EnvCtrlEnum_defaultEncoding_type decode_to_execute_ENV_CTRL;
  reg        `EnvCtrlEnum_defaultEncoding_type execute_to_memory_ENV_CTRL;
  reg        `EnvCtrlEnum_defaultEncoding_type memory_to_writeBack_ENV_CTRL;
  reg        `AluCtrlEnum_defaultEncoding_type decode_to_execute_ALU_CTRL;
  reg        [31:0]   memory_to_writeBack_MEMORY_READ_DATA;
  reg                 decode_to_execute_SRC_USE_SUB_LESS;
  reg        [31:0]   decode_to_execute_INSTRUCTION;
  reg        [31:0]   execute_to_memory_INSTRUCTION;
  reg        [31:0]   memory_to_writeBack_INSTRUCTION;
  reg        [1:0]    execute_to_memory_MEMORY_ADDRESS_LOW;
  reg        [1:0]    memory_to_writeBack_MEMORY_ADDRESS_LOW;
  reg                 decode_to_execute_DO_EBREAK;
  reg                 decode_to_execute_SRC2_FORCE_ZERO;
  reg                 decode_to_execute_MEMORY_STORE;
  reg                 execute_to_memory_MEMORY_STORE;
  reg                 memory_to_writeBack_MEMORY_STORE;
  reg                 decode_to_execute_REGFILE_WRITE_VALID;
  reg                 execute_to_memory_REGFILE_WRITE_VALID;
  reg                 memory_to_writeBack_REGFILE_WRITE_VALID;
  reg        [33:0]   execute_to_memory_MUL_LH;
  reg        [31:0]   decode_to_execute_RS2;
  reg                 decode_to_execute_IS_CSR;
  reg                 decode_to_execute_IS_DIV;
  reg                 execute_to_memory_IS_DIV;
  reg        [31:0]   decode_to_execute_FORMAL_PC_NEXT;
  reg        [31:0]   execute_to_memory_FORMAL_PC_NEXT;
  reg        [31:0]   memory_to_writeBack_FORMAL_PC_NEXT;
  reg        [51:0]   memory_to_writeBack_MUL_LOW;
  reg                 decode_to_execute_SRC_LESS_UNSIGNED;
  reg                 decode_to_execute_CSR_WRITE_OPCODE;
  reg                 decode_to_execute_BYPASSABLE_MEMORY_STAGE;
  reg                 execute_to_memory_BYPASSABLE_MEMORY_STAGE;
  reg        [33:0]   execute_to_memory_MUL_HH;
  reg        [33:0]   memory_to_writeBack_MUL_HH;
  reg                 decode_to_execute_IS_MUL;
  reg                 execute_to_memory_IS_MUL;
  reg                 memory_to_writeBack_IS_MUL;
  reg        [31:0]   decode_to_execute_PC;
  reg        [31:0]   execute_to_memory_PC;
  reg        [31:0]   memory_to_writeBack_PC;
  reg        `Src2CtrlEnum_defaultEncoding_type decode_to_execute_SRC2_CTRL;
  reg        `AluBitwiseCtrlEnum_defaultEncoding_type decode_to_execute_ALU_BITWISE_CTRL;
  reg        [2:0]    _zz_129_;
  reg                 execute_CsrPlugin_csr_3857;
  reg                 execute_CsrPlugin_csr_3858;
  reg                 execute_CsrPlugin_csr_3859;
  reg                 execute_CsrPlugin_csr_3860;
  reg                 execute_CsrPlugin_csr_769;
  reg                 execute_CsrPlugin_csr_768;
  reg                 execute_CsrPlugin_csr_836;
  reg                 execute_CsrPlugin_csr_772;
  reg                 execute_CsrPlugin_csr_773;
  reg                 execute_CsrPlugin_csr_833;
  reg                 execute_CsrPlugin_csr_832;
  reg                 execute_CsrPlugin_csr_834;
  reg                 execute_CsrPlugin_csr_835;
  reg                 execute_CsrPlugin_csr_2816;
  reg                 execute_CsrPlugin_csr_2944;
  reg                 execute_CsrPlugin_csr_2818;
  reg                 execute_CsrPlugin_csr_2946;
  reg                 execute_CsrPlugin_csr_3072;
  reg                 execute_CsrPlugin_csr_3200;
  reg        [31:0]   _zz_130_;
  reg        [31:0]   _zz_131_;
  reg        [31:0]   _zz_132_;
  reg        [31:0]   _zz_133_;
  reg        [31:0]   _zz_134_;
  reg        [31:0]   _zz_135_;
  reg        [31:0]   _zz_136_;
  reg        [31:0]   _zz_137_;
  reg        [31:0]   _zz_138_;
  reg        [31:0]   _zz_139_;
  reg        [31:0]   _zz_140_;
  reg        [31:0]   _zz_141_;
  reg        [31:0]   _zz_142_;
  reg        [31:0]   _zz_143_;
  reg        [31:0]   _zz_144_;
  `ifndef SYNTHESIS
  reg [39:0] decode_ALU_BITWISE_CTRL_string;
  reg [39:0] _zz_1__string;
  reg [39:0] _zz_2__string;
  reg [39:0] _zz_3__string;
  reg [23:0] decode_SRC2_CTRL_string;
  reg [23:0] _zz_4__string;
  reg [23:0] _zz_5__string;
  reg [23:0] _zz_6__string;
  reg [63:0] decode_ALU_CTRL_string;
  reg [63:0] _zz_7__string;
  reg [63:0] _zz_8__string;
  reg [63:0] _zz_9__string;
  reg [39:0] _zz_10__string;
  reg [39:0] _zz_11__string;
  reg [39:0] _zz_12__string;
  reg [39:0] _zz_13__string;
  reg [39:0] decode_ENV_CTRL_string;
  reg [39:0] _zz_14__string;
  reg [39:0] _zz_15__string;
  reg [39:0] _zz_16__string;
  reg [95:0] decode_SRC1_CTRL_string;
  reg [95:0] _zz_17__string;
  reg [95:0] _zz_18__string;
  reg [95:0] _zz_19__string;
  reg [31:0] decode_BRANCH_CTRL_string;
  reg [31:0] _zz_20__string;
  reg [31:0] _zz_21__string;
  reg [31:0] _zz_22__string;
  reg [71:0] decode_SHIFT_CTRL_string;
  reg [71:0] _zz_23__string;
  reg [71:0] _zz_24__string;
  reg [71:0] _zz_25__string;
  reg [31:0] execute_BRANCH_CTRL_string;
  reg [31:0] _zz_26__string;
  reg [39:0] memory_ENV_CTRL_string;
  reg [39:0] _zz_27__string;
  reg [39:0] execute_ENV_CTRL_string;
  reg [39:0] _zz_28__string;
  reg [39:0] writeBack_ENV_CTRL_string;
  reg [39:0] _zz_29__string;
  reg [71:0] execute_SHIFT_CTRL_string;
  reg [71:0] _zz_32__string;
  reg [23:0] execute_SRC2_CTRL_string;
  reg [23:0] _zz_34__string;
  reg [95:0] execute_SRC1_CTRL_string;
  reg [95:0] _zz_35__string;
  reg [63:0] execute_ALU_CTRL_string;
  reg [63:0] _zz_36__string;
  reg [39:0] execute_ALU_BITWISE_CTRL_string;
  reg [39:0] _zz_37__string;
  reg [71:0] _zz_41__string;
  reg [39:0] _zz_42__string;
  reg [63:0] _zz_43__string;
  reg [23:0] _zz_44__string;
  reg [39:0] _zz_45__string;
  reg [95:0] _zz_46__string;
  reg [31:0] _zz_47__string;
  reg [31:0] _zz_80__string;
  reg [95:0] _zz_81__string;
  reg [39:0] _zz_82__string;
  reg [23:0] _zz_83__string;
  reg [63:0] _zz_84__string;
  reg [39:0] _zz_85__string;
  reg [71:0] _zz_86__string;
  reg [71:0] decode_to_execute_SHIFT_CTRL_string;
  reg [31:0] decode_to_execute_BRANCH_CTRL_string;
  reg [95:0] decode_to_execute_SRC1_CTRL_string;
  reg [39:0] decode_to_execute_ENV_CTRL_string;
  reg [39:0] execute_to_memory_ENV_CTRL_string;
  reg [39:0] memory_to_writeBack_ENV_CTRL_string;
  reg [63:0] decode_to_execute_ALU_CTRL_string;
  reg [23:0] decode_to_execute_SRC2_CTRL_string;
  reg [39:0] decode_to_execute_ALU_BITWISE_CTRL_string;
  `endif

  reg [31:0] RegFilePlugin_regFile [0:31] /* verilator public */ ;

  assign _zz_149_ = (memory_arbitration_isValid && memory_IS_DIV);
  assign _zz_150_ = (writeBack_arbitration_isValid && writeBack_REGFILE_WRITE_VALID);
  assign _zz_151_ = 1'b1;
  assign _zz_152_ = (memory_arbitration_isValid && memory_REGFILE_WRITE_VALID);
  assign _zz_153_ = (execute_arbitration_isValid && execute_REGFILE_WRITE_VALID);
  assign _zz_154_ = (execute_arbitration_isValid && execute_IS_CSR);
  assign _zz_155_ = (execute_arbitration_isValid && (execute_ENV_CTRL == `EnvCtrlEnum_defaultEncoding_WFI));
  assign _zz_156_ = (execute_arbitration_isValid && execute_DO_EBREAK);
  assign _zz_157_ = ({BranchPlugin_branchExceptionPort_valid,CsrPlugin_selfException_valid} != (2'b00));
  assign _zz_158_ = (({writeBack_arbitration_isValid,memory_arbitration_isValid} != (2'b00)) == 1'b0);
  assign _zz_159_ = (CsrPlugin_hadException || CsrPlugin_interruptJump);
  assign _zz_160_ = (writeBack_arbitration_isValid && (writeBack_ENV_CTRL == `EnvCtrlEnum_defaultEncoding_XRET));
  assign _zz_161_ = (DebugPlugin_stepIt && IBusSimplePlugin_incomingInstruction);
  assign _zz_162_ = writeBack_INSTRUCTION[29 : 28];
  assign _zz_163_ = (writeBack_arbitration_isValid && writeBack_REGFILE_WRITE_VALID);
  assign _zz_164_ = (1'b0 || (! 1'b1));
  assign _zz_165_ = (memory_arbitration_isValid && memory_REGFILE_WRITE_VALID);
  assign _zz_166_ = (1'b0 || (! memory_BYPASSABLE_MEMORY_STAGE));
  assign _zz_167_ = (execute_arbitration_isValid && execute_REGFILE_WRITE_VALID);
  assign _zz_168_ = (1'b0 || (! execute_BYPASSABLE_EXECUTE_STAGE));
  assign _zz_169_ = execute_INSTRUCTION[13 : 12];
  assign _zz_170_ = (memory_DivPlugin_frontendOk && (! memory_DivPlugin_div_done));
  assign _zz_171_ = (! memory_arbitration_isStuck);
  assign _zz_172_ = (execute_CsrPlugin_illegalAccess || execute_CsrPlugin_illegalInstruction);
  assign _zz_173_ = (execute_arbitration_isValid && (execute_ENV_CTRL == `EnvCtrlEnum_defaultEncoding_ECALL));
  assign _zz_174_ = debug_bus_cmd_payload_address[7 : 2];
  assign _zz_175_ = (CsrPlugin_mstatus_MIE || (CsrPlugin_privilege < (2'b11)));
  assign _zz_176_ = ((_zz_113_ && 1'b1) && (! 1'b0));
  assign _zz_177_ = ((_zz_114_ && 1'b1) && (! 1'b0));
  assign _zz_178_ = ((_zz_115_ && 1'b1) && (! 1'b0));
  assign _zz_179_ = writeBack_INSTRUCTION[13 : 12];
  assign _zz_180_ = writeBack_INSTRUCTION[13 : 12];
  assign _zz_181_ = execute_INSTRUCTION[13];
  assign _zz_182_ = _zz_73_[12 : 12];
  assign _zz_183_ = _zz_73_[0 : 0];
  assign _zz_184_ = _zz_73_[6 : 6];
  assign _zz_185_ = ($signed(_zz_186_) + $signed(_zz_191_));
  assign _zz_186_ = ($signed(_zz_187_) + $signed(_zz_189_));
  assign _zz_187_ = 52'h0;
  assign _zz_188_ = {1'b0,memory_MUL_LL};
  assign _zz_189_ = {{19{_zz_188_[32]}}, _zz_188_};
  assign _zz_190_ = ({16'd0,memory_MUL_LH} <<< 16);
  assign _zz_191_ = {{2{_zz_190_[49]}}, _zz_190_};
  assign _zz_192_ = ({16'd0,memory_MUL_HL} <<< 16);
  assign _zz_193_ = {{2{_zz_192_[49]}}, _zz_192_};
  assign _zz_194_ = _zz_73_[19 : 19];
  assign _zz_195_ = _zz_73_[22 : 22];
  assign _zz_196_ = _zz_73_[13 : 13];
  assign _zz_197_ = _zz_73_[5 : 5];
  assign _zz_198_ = _zz_73_[3 : 3];
  assign _zz_199_ = _zz_73_[16 : 16];
  assign _zz_200_ = _zz_73_[27 : 27];
  assign _zz_201_ = _zz_73_[23 : 23];
  assign _zz_202_ = _zz_73_[4 : 4];
  assign _zz_203_ = _zz_73_[24 : 24];
  assign _zz_204_ = ($signed(_zz_206_) >>> execute_FullBarrelShifterPlugin_amplitude);
  assign _zz_205_ = _zz_204_[31 : 0];
  assign _zz_206_ = {((execute_SHIFT_CTRL == `ShiftCtrlEnum_defaultEncoding_SRA_1) && execute_FullBarrelShifterPlugin_reversed[31]),execute_FullBarrelShifterPlugin_reversed};
  assign _zz_207_ = _zz_73_[9 : 9];
  assign _zz_208_ = _zz_73_[2 : 2];
  assign _zz_209_ = _zz_73_[28 : 28];
  assign _zz_210_ = (_zz_50_ & (~ _zz_211_));
  assign _zz_211_ = (_zz_50_ - (2'b01));
  assign _zz_212_ = {IBusSimplePlugin_fetchPc_inc,(2'b00)};
  assign _zz_213_ = {29'd0, _zz_212_};
  assign _zz_214_ = (IBusSimplePlugin_pending_value + _zz_216_);
  assign _zz_215_ = IBusSimplePlugin_pending_inc;
  assign _zz_216_ = {2'd0, _zz_215_};
  assign _zz_217_ = IBusSimplePlugin_pending_dec;
  assign _zz_218_ = {2'd0, _zz_217_};
  assign _zz_219_ = (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid && (IBusSimplePlugin_rspJoin_rspBuffer_discardCounter != (3'b000)));
  assign _zz_220_ = {2'd0, _zz_219_};
  assign _zz_221_ = execute_SRC_LESS;
  assign _zz_222_ = (3'b100);
  assign _zz_223_ = execute_INSTRUCTION[19 : 15];
  assign _zz_224_ = execute_INSTRUCTION[31 : 20];
  assign _zz_225_ = {execute_INSTRUCTION[31 : 25],execute_INSTRUCTION[11 : 7]};
  assign _zz_226_ = ($signed(_zz_227_) + $signed(_zz_230_));
  assign _zz_227_ = ($signed(_zz_228_) + $signed(_zz_229_));
  assign _zz_228_ = execute_SRC1;
  assign _zz_229_ = (execute_SRC_USE_SUB_LESS ? (~ execute_SRC2) : execute_SRC2);
  assign _zz_230_ = (execute_SRC_USE_SUB_LESS ? _zz_231_ : _zz_232_);
  assign _zz_231_ = 32'h00000001;
  assign _zz_232_ = 32'h0;
  assign _zz_233_ = {{14{writeBack_MUL_LOW[51]}}, writeBack_MUL_LOW};
  assign _zz_234_ = ({32'd0,writeBack_MUL_HH} <<< 32);
  assign _zz_235_ = writeBack_MUL_LOW[31 : 0];
  assign _zz_236_ = writeBack_MulPlugin_result[63 : 32];
  assign _zz_237_ = memory_DivPlugin_div_counter_willIncrement;
  assign _zz_238_ = {5'd0, _zz_237_};
  assign _zz_239_ = {1'd0, memory_DivPlugin_rs2};
  assign _zz_240_ = memory_DivPlugin_div_stage_0_remainderMinusDenominator[31:0];
  assign _zz_241_ = memory_DivPlugin_div_stage_0_remainderShifted[31:0];
  assign _zz_242_ = {_zz_108_,(! memory_DivPlugin_div_stage_0_remainderMinusDenominator[32])};
  assign _zz_243_ = _zz_244_;
  assign _zz_244_ = _zz_245_;
  assign _zz_245_ = ({1'b0,(memory_DivPlugin_div_needRevert ? (~ _zz_109_) : _zz_109_)} + _zz_247_);
  assign _zz_246_ = memory_DivPlugin_div_needRevert;
  assign _zz_247_ = {32'd0, _zz_246_};
  assign _zz_248_ = _zz_111_;
  assign _zz_249_ = {32'd0, _zz_248_};
  assign _zz_250_ = _zz_110_;
  assign _zz_251_ = {31'd0, _zz_250_};
  assign _zz_252_ = (_zz_116_ & (~ _zz_253_));
  assign _zz_253_ = (_zz_116_ - (2'b01));
  assign _zz_254_ = {{{execute_INSTRUCTION[31],execute_INSTRUCTION[19 : 12]},execute_INSTRUCTION[20]},execute_INSTRUCTION[30 : 21]};
  assign _zz_255_ = execute_INSTRUCTION[31 : 20];
  assign _zz_256_ = {{{execute_INSTRUCTION[31],execute_INSTRUCTION[7]},execute_INSTRUCTION[30 : 25]},execute_INSTRUCTION[11 : 8]};
  assign _zz_257_ = execute_CsrPlugin_writeData[7 : 7];
  assign _zz_258_ = execute_CsrPlugin_writeData[3 : 3];
  assign _zz_259_ = execute_CsrPlugin_writeData[3 : 3];
  assign _zz_260_ = execute_CsrPlugin_writeData[11 : 11];
  assign _zz_261_ = execute_CsrPlugin_writeData[7 : 7];
  assign _zz_262_ = execute_CsrPlugin_writeData[3 : 3];
  assign _zz_263_ = execute_CsrPlugin_writeData[31 : 31];
  assign _zz_264_ = 32'h0000107f;
  assign _zz_265_ = (decode_INSTRUCTION & 32'h0000207f);
  assign _zz_266_ = 32'h00002073;
  assign _zz_267_ = ((decode_INSTRUCTION & 32'h0000407f) == 32'h00004063);
  assign _zz_268_ = ((decode_INSTRUCTION & 32'h0000207f) == 32'h00002013);
  assign _zz_269_ = {((decode_INSTRUCTION & 32'h0000603f) == 32'h00000023),{((decode_INSTRUCTION & 32'h0000207f) == 32'h00000003),{((decode_INSTRUCTION & _zz_270_) == 32'h00000003),{(_zz_271_ == _zz_272_),{_zz_273_,{_zz_274_,_zz_275_}}}}}};
  assign _zz_270_ = 32'h0000505f;
  assign _zz_271_ = (decode_INSTRUCTION & 32'h0000707b);
  assign _zz_272_ = 32'h00000063;
  assign _zz_273_ = ((decode_INSTRUCTION & 32'h0000607f) == 32'h0000000f);
  assign _zz_274_ = ((decode_INSTRUCTION & 32'hfc00007f) == 32'h00000033);
  assign _zz_275_ = {((decode_INSTRUCTION & 32'hbc00707f) == 32'h00005013),{((decode_INSTRUCTION & 32'hfc00307f) == 32'h00001013),{((decode_INSTRUCTION & _zz_276_) == 32'h00005033),{(_zz_277_ == _zz_278_),{_zz_279_,{_zz_280_,_zz_281_}}}}}};
  assign _zz_276_ = 32'hbe00707f;
  assign _zz_277_ = (decode_INSTRUCTION & 32'hbe00707f);
  assign _zz_278_ = 32'h00000033;
  assign _zz_279_ = ((decode_INSTRUCTION & 32'hdfffffff) == 32'h10200073);
  assign _zz_280_ = ((decode_INSTRUCTION & 32'hffefffff) == 32'h00000073);
  assign _zz_281_ = ((decode_INSTRUCTION & 32'hffffffff) == 32'h10500073);
  assign _zz_282_ = (decode_INSTRUCTION & 32'h00007034);
  assign _zz_283_ = 32'h00005010;
  assign _zz_284_ = (decode_INSTRUCTION & 32'h02007064);
  assign _zz_285_ = 32'h00005020;
  assign _zz_286_ = ((decode_INSTRUCTION & 32'h40003054) == 32'h40001010);
  assign _zz_287_ = ((decode_INSTRUCTION & _zz_296_) == 32'h00001010);
  assign _zz_288_ = ((decode_INSTRUCTION & _zz_297_) == 32'h00001010);
  assign _zz_289_ = _zz_77_;
  assign _zz_290_ = {(_zz_298_ == _zz_299_),{_zz_300_,{_zz_301_,_zz_302_}}};
  assign _zz_291_ = _zz_76_;
  assign _zz_292_ = (1'b0);
  assign _zz_293_ = ({_zz_303_,_zz_304_} != (2'b00));
  assign _zz_294_ = (_zz_305_ != (1'b0));
  assign _zz_295_ = {(_zz_306_ != _zz_307_),{_zz_308_,{_zz_309_,_zz_310_}}};
  assign _zz_296_ = 32'h00007034;
  assign _zz_297_ = 32'h02007054;
  assign _zz_298_ = (decode_INSTRUCTION & 32'h00001010);
  assign _zz_299_ = 32'h00001010;
  assign _zz_300_ = ((decode_INSTRUCTION & _zz_311_) == 32'h00002010);
  assign _zz_301_ = (_zz_312_ == _zz_313_);
  assign _zz_302_ = {_zz_314_,_zz_315_};
  assign _zz_303_ = ((decode_INSTRUCTION & _zz_316_) == 32'h10000050);
  assign _zz_304_ = ((decode_INSTRUCTION & _zz_317_) == 32'h00000050);
  assign _zz_305_ = ((decode_INSTRUCTION & _zz_318_) == 32'h00000050);
  assign _zz_306_ = {_zz_319_,{_zz_320_,_zz_321_}};
  assign _zz_307_ = (4'b0000);
  assign _zz_308_ = (_zz_322_ != (1'b0));
  assign _zz_309_ = (_zz_323_ != _zz_324_);
  assign _zz_310_ = {_zz_325_,{_zz_326_,_zz_327_}};
  assign _zz_311_ = 32'h00002010;
  assign _zz_312_ = (decode_INSTRUCTION & 32'h00000050);
  assign _zz_313_ = 32'h00000010;
  assign _zz_314_ = ((decode_INSTRUCTION & _zz_328_) == 32'h00000004);
  assign _zz_315_ = ((decode_INSTRUCTION & _zz_329_) == 32'h0);
  assign _zz_316_ = 32'h10203050;
  assign _zz_317_ = 32'h10103050;
  assign _zz_318_ = 32'h00103050;
  assign _zz_319_ = ((decode_INSTRUCTION & _zz_330_) == 32'h0);
  assign _zz_320_ = (_zz_331_ == _zz_332_);
  assign _zz_321_ = {_zz_79_,_zz_333_};
  assign _zz_322_ = ((decode_INSTRUCTION & _zz_334_) == 32'h00100050);
  assign _zz_323_ = {_zz_335_,_zz_336_};
  assign _zz_324_ = (2'b00);
  assign _zz_325_ = (_zz_337_ != (1'b0));
  assign _zz_326_ = (_zz_338_ != _zz_339_);
  assign _zz_327_ = {_zz_340_,{_zz_341_,_zz_342_}};
  assign _zz_328_ = 32'h0000000c;
  assign _zz_329_ = 32'h00000028;
  assign _zz_330_ = 32'h00000044;
  assign _zz_331_ = (decode_INSTRUCTION & 32'h00000018);
  assign _zz_332_ = 32'h0;
  assign _zz_333_ = ((decode_INSTRUCTION & 32'h00005004) == 32'h00001000);
  assign _zz_334_ = 32'h10103050;
  assign _zz_335_ = ((decode_INSTRUCTION & 32'h00001050) == 32'h00001050);
  assign _zz_336_ = ((decode_INSTRUCTION & 32'h00002050) == 32'h00002050);
  assign _zz_337_ = ((decode_INSTRUCTION & 32'h00004004) == 32'h00004000);
  assign _zz_338_ = _zz_79_;
  assign _zz_339_ = (1'b0);
  assign _zz_340_ = ((_zz_343_ == _zz_344_) != (1'b0));
  assign _zz_341_ = ({_zz_345_,_zz_346_} != (2'b00));
  assign _zz_342_ = {(_zz_347_ != _zz_348_),{_zz_349_,{_zz_350_,_zz_351_}}};
  assign _zz_343_ = (decode_INSTRUCTION & 32'h02004064);
  assign _zz_344_ = 32'h02004020;
  assign _zz_345_ = _zz_75_;
  assign _zz_346_ = ((decode_INSTRUCTION & _zz_352_) == 32'h00000020);
  assign _zz_347_ = {_zz_75_,(_zz_353_ == _zz_354_)};
  assign _zz_348_ = (2'b00);
  assign _zz_349_ = ({_zz_75_,{_zz_355_,_zz_356_}} != (3'b000));
  assign _zz_350_ = (_zz_357_ != (1'b0));
  assign _zz_351_ = {(_zz_358_ != _zz_359_),{_zz_360_,{_zz_361_,_zz_362_}}};
  assign _zz_352_ = 32'h00000070;
  assign _zz_353_ = (decode_INSTRUCTION & 32'h00000020);
  assign _zz_354_ = 32'h0;
  assign _zz_355_ = _zz_74_;
  assign _zz_356_ = ((decode_INSTRUCTION & _zz_363_) == 32'h00000020);
  assign _zz_357_ = ((decode_INSTRUCTION & 32'h00001000) == 32'h00001000);
  assign _zz_358_ = ((decode_INSTRUCTION & _zz_364_) == 32'h00002000);
  assign _zz_359_ = (1'b0);
  assign _zz_360_ = ((_zz_365_ == _zz_366_) != (1'b0));
  assign _zz_361_ = (_zz_367_ != (1'b0));
  assign _zz_362_ = {(_zz_368_ != _zz_369_),{_zz_370_,{_zz_371_,_zz_372_}}};
  assign _zz_363_ = 32'h02000060;
  assign _zz_364_ = 32'h00003000;
  assign _zz_365_ = (decode_INSTRUCTION & 32'h00000020);
  assign _zz_366_ = 32'h00000020;
  assign _zz_367_ = ((decode_INSTRUCTION & 32'h02004074) == 32'h02000030);
  assign _zz_368_ = {((decode_INSTRUCTION & _zz_373_) == 32'h00000004),_zz_78_};
  assign _zz_369_ = (2'b00);
  assign _zz_370_ = ({(_zz_374_ == _zz_375_),_zz_78_} != (2'b00));
  assign _zz_371_ = ({_zz_376_,{_zz_377_,_zz_378_}} != (3'b000));
  assign _zz_372_ = {({_zz_379_,_zz_380_} != (2'b00)),{(_zz_381_ != _zz_382_),{_zz_383_,{_zz_384_,_zz_385_}}}};
  assign _zz_373_ = 32'h00000014;
  assign _zz_374_ = (decode_INSTRUCTION & 32'h00000044);
  assign _zz_375_ = 32'h00000004;
  assign _zz_376_ = ((decode_INSTRUCTION & 32'h00000044) == 32'h00000040);
  assign _zz_377_ = ((decode_INSTRUCTION & _zz_386_) == 32'h00002010);
  assign _zz_378_ = ((decode_INSTRUCTION & _zz_387_) == 32'h40000030);
  assign _zz_379_ = _zz_77_;
  assign _zz_380_ = ((decode_INSTRUCTION & _zz_388_) == 32'h00000004);
  assign _zz_381_ = ((decode_INSTRUCTION & _zz_389_) == 32'h00000040);
  assign _zz_382_ = (1'b0);
  assign _zz_383_ = ({_zz_390_,_zz_391_} != (2'b00));
  assign _zz_384_ = (_zz_392_ != (1'b0));
  assign _zz_385_ = {(_zz_393_ != _zz_394_),{_zz_395_,{_zz_396_,_zz_397_}}};
  assign _zz_386_ = 32'h00002014;
  assign _zz_387_ = 32'h40000034;
  assign _zz_388_ = 32'h0000001c;
  assign _zz_389_ = 32'h00000058;
  assign _zz_390_ = ((decode_INSTRUCTION & 32'h00002010) == 32'h00002000);
  assign _zz_391_ = ((decode_INSTRUCTION & 32'h00005000) == 32'h00001000);
  assign _zz_392_ = ((decode_INSTRUCTION & 32'h00000058) == 32'h0);
  assign _zz_393_ = {(_zz_398_ == _zz_399_),(_zz_400_ == _zz_401_)};
  assign _zz_394_ = (2'b00);
  assign _zz_395_ = (_zz_76_ != (1'b0));
  assign _zz_396_ = (_zz_402_ != (1'b0));
  assign _zz_397_ = {(_zz_403_ != _zz_404_),(_zz_405_ != _zz_406_)};
  assign _zz_398_ = (decode_INSTRUCTION & 32'h00000034);
  assign _zz_399_ = 32'h00000020;
  assign _zz_400_ = (decode_INSTRUCTION & 32'h00000064);
  assign _zz_401_ = 32'h00000020;
  assign _zz_402_ = ((decode_INSTRUCTION & 32'h00000064) == 32'h00000024);
  assign _zz_403_ = {((decode_INSTRUCTION & _zz_407_) == 32'h00002040),{(_zz_408_ == _zz_409_),{_zz_410_,_zz_411_}}};
  assign _zz_404_ = (4'b0000);
  assign _zz_405_ = {((decode_INSTRUCTION & _zz_412_) == 32'h00000040),{_zz_75_,{_zz_413_,{_zz_414_,_zz_415_}}}};
  assign _zz_406_ = 5'h0;
  assign _zz_407_ = 32'h00002040;
  assign _zz_408_ = (decode_INSTRUCTION & 32'h00001040);
  assign _zz_409_ = 32'h00001040;
  assign _zz_410_ = ((decode_INSTRUCTION & 32'h00100040) == 32'h00000040);
  assign _zz_411_ = ((decode_INSTRUCTION & 32'h00000050) == 32'h00000040);
  assign _zz_412_ = 32'h00000040;
  assign _zz_413_ = ((decode_INSTRUCTION & 32'h00004020) == 32'h00004020);
  assign _zz_414_ = _zz_74_;
  assign _zz_415_ = ((decode_INSTRUCTION & 32'h02000020) == 32'h00000020);
  assign _zz_416_ = 32'h0;
  assign _zz_417_ = 32'h0;
  assign _zz_418_ = 32'h0;
  assign _zz_419_ = 32'h0;
  assign _zz_147_ = RegFilePlugin_regFile[decode_RegFilePlugin_regFileReadAddress1];
  assign _zz_148_ = RegFilePlugin_regFile[decode_RegFilePlugin_regFileReadAddress2];
  always @ (posedge io_clock) begin
    if(_zz_40_) begin
      RegFilePlugin_regFile[lastStageRegFileWrite_payload_address] <= lastStageRegFileWrite_payload_data;
    end
  end

  StreamFifoLowLatency IBusSimplePlugin_rspJoin_rspBuffer_c ( 
    .io_push_valid            (iBus_rsp_valid                                                  ), //i
    .io_push_ready            (IBusSimplePlugin_rspJoin_rspBuffer_c_io_push_ready              ), //o
    .io_push_payload_error    (iBus_rsp_payload_error                                          ), //i
    .io_push_payload_inst     (iBus_rsp_payload_inst[31:0]                                     ), //i
    .io_pop_valid             (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid               ), //o
    .io_pop_ready             (_zz_145_                                                        ), //i
    .io_pop_payload_error     (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_error       ), //o
    .io_pop_payload_inst      (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_inst[31:0]  ), //o
    .io_flush                 (_zz_146_                                                        ), //i
    .io_occupancy             (IBusSimplePlugin_rspJoin_rspBuffer_c_io_occupancy[1:0]          ), //o
    .io_clock                 (io_clock                                                        ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                                           )  //i
  );
  `ifndef SYNTHESIS
  always @(*) begin
    case(decode_ALU_BITWISE_CTRL)
      `AluBitwiseCtrlEnum_defaultEncoding_XOR_1 : decode_ALU_BITWISE_CTRL_string = "XOR_1";
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : decode_ALU_BITWISE_CTRL_string = "OR_1 ";
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : decode_ALU_BITWISE_CTRL_string = "AND_1";
      default : decode_ALU_BITWISE_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_1_)
      `AluBitwiseCtrlEnum_defaultEncoding_XOR_1 : _zz_1__string = "XOR_1";
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : _zz_1__string = "OR_1 ";
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : _zz_1__string = "AND_1";
      default : _zz_1__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_2_)
      `AluBitwiseCtrlEnum_defaultEncoding_XOR_1 : _zz_2__string = "XOR_1";
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : _zz_2__string = "OR_1 ";
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : _zz_2__string = "AND_1";
      default : _zz_2__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_3_)
      `AluBitwiseCtrlEnum_defaultEncoding_XOR_1 : _zz_3__string = "XOR_1";
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : _zz_3__string = "OR_1 ";
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : _zz_3__string = "AND_1";
      default : _zz_3__string = "?????";
    endcase
  end
  always @(*) begin
    case(decode_SRC2_CTRL)
      `Src2CtrlEnum_defaultEncoding_RS : decode_SRC2_CTRL_string = "RS ";
      `Src2CtrlEnum_defaultEncoding_IMI : decode_SRC2_CTRL_string = "IMI";
      `Src2CtrlEnum_defaultEncoding_IMS : decode_SRC2_CTRL_string = "IMS";
      `Src2CtrlEnum_defaultEncoding_PC : decode_SRC2_CTRL_string = "PC ";
      default : decode_SRC2_CTRL_string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_4_)
      `Src2CtrlEnum_defaultEncoding_RS : _zz_4__string = "RS ";
      `Src2CtrlEnum_defaultEncoding_IMI : _zz_4__string = "IMI";
      `Src2CtrlEnum_defaultEncoding_IMS : _zz_4__string = "IMS";
      `Src2CtrlEnum_defaultEncoding_PC : _zz_4__string = "PC ";
      default : _zz_4__string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_5_)
      `Src2CtrlEnum_defaultEncoding_RS : _zz_5__string = "RS ";
      `Src2CtrlEnum_defaultEncoding_IMI : _zz_5__string = "IMI";
      `Src2CtrlEnum_defaultEncoding_IMS : _zz_5__string = "IMS";
      `Src2CtrlEnum_defaultEncoding_PC : _zz_5__string = "PC ";
      default : _zz_5__string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_6_)
      `Src2CtrlEnum_defaultEncoding_RS : _zz_6__string = "RS ";
      `Src2CtrlEnum_defaultEncoding_IMI : _zz_6__string = "IMI";
      `Src2CtrlEnum_defaultEncoding_IMS : _zz_6__string = "IMS";
      `Src2CtrlEnum_defaultEncoding_PC : _zz_6__string = "PC ";
      default : _zz_6__string = "???";
    endcase
  end
  always @(*) begin
    case(decode_ALU_CTRL)
      `AluCtrlEnum_defaultEncoding_ADD_SUB : decode_ALU_CTRL_string = "ADD_SUB ";
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : decode_ALU_CTRL_string = "SLT_SLTU";
      `AluCtrlEnum_defaultEncoding_BITWISE : decode_ALU_CTRL_string = "BITWISE ";
      default : decode_ALU_CTRL_string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_7_)
      `AluCtrlEnum_defaultEncoding_ADD_SUB : _zz_7__string = "ADD_SUB ";
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : _zz_7__string = "SLT_SLTU";
      `AluCtrlEnum_defaultEncoding_BITWISE : _zz_7__string = "BITWISE ";
      default : _zz_7__string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_8_)
      `AluCtrlEnum_defaultEncoding_ADD_SUB : _zz_8__string = "ADD_SUB ";
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : _zz_8__string = "SLT_SLTU";
      `AluCtrlEnum_defaultEncoding_BITWISE : _zz_8__string = "BITWISE ";
      default : _zz_8__string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_9_)
      `AluCtrlEnum_defaultEncoding_ADD_SUB : _zz_9__string = "ADD_SUB ";
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : _zz_9__string = "SLT_SLTU";
      `AluCtrlEnum_defaultEncoding_BITWISE : _zz_9__string = "BITWISE ";
      default : _zz_9__string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_10_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_10__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_10__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_10__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_10__string = "ECALL";
      default : _zz_10__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_11_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_11__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_11__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_11__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_11__string = "ECALL";
      default : _zz_11__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_12_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_12__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_12__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_12__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_12__string = "ECALL";
      default : _zz_12__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_13_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_13__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_13__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_13__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_13__string = "ECALL";
      default : _zz_13__string = "?????";
    endcase
  end
  always @(*) begin
    case(decode_ENV_CTRL)
      `EnvCtrlEnum_defaultEncoding_NONE : decode_ENV_CTRL_string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : decode_ENV_CTRL_string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : decode_ENV_CTRL_string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : decode_ENV_CTRL_string = "ECALL";
      default : decode_ENV_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_14_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_14__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_14__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_14__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_14__string = "ECALL";
      default : _zz_14__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_15_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_15__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_15__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_15__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_15__string = "ECALL";
      default : _zz_15__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_16_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_16__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_16__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_16__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_16__string = "ECALL";
      default : _zz_16__string = "?????";
    endcase
  end
  always @(*) begin
    case(decode_SRC1_CTRL)
      `Src1CtrlEnum_defaultEncoding_RS : decode_SRC1_CTRL_string = "RS          ";
      `Src1CtrlEnum_defaultEncoding_IMU : decode_SRC1_CTRL_string = "IMU         ";
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : decode_SRC1_CTRL_string = "PC_INCREMENT";
      `Src1CtrlEnum_defaultEncoding_URS1 : decode_SRC1_CTRL_string = "URS1        ";
      default : decode_SRC1_CTRL_string = "????????????";
    endcase
  end
  always @(*) begin
    case(_zz_17_)
      `Src1CtrlEnum_defaultEncoding_RS : _zz_17__string = "RS          ";
      `Src1CtrlEnum_defaultEncoding_IMU : _zz_17__string = "IMU         ";
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : _zz_17__string = "PC_INCREMENT";
      `Src1CtrlEnum_defaultEncoding_URS1 : _zz_17__string = "URS1        ";
      default : _zz_17__string = "????????????";
    endcase
  end
  always @(*) begin
    case(_zz_18_)
      `Src1CtrlEnum_defaultEncoding_RS : _zz_18__string = "RS          ";
      `Src1CtrlEnum_defaultEncoding_IMU : _zz_18__string = "IMU         ";
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : _zz_18__string = "PC_INCREMENT";
      `Src1CtrlEnum_defaultEncoding_URS1 : _zz_18__string = "URS1        ";
      default : _zz_18__string = "????????????";
    endcase
  end
  always @(*) begin
    case(_zz_19_)
      `Src1CtrlEnum_defaultEncoding_RS : _zz_19__string = "RS          ";
      `Src1CtrlEnum_defaultEncoding_IMU : _zz_19__string = "IMU         ";
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : _zz_19__string = "PC_INCREMENT";
      `Src1CtrlEnum_defaultEncoding_URS1 : _zz_19__string = "URS1        ";
      default : _zz_19__string = "????????????";
    endcase
  end
  always @(*) begin
    case(decode_BRANCH_CTRL)
      `BranchCtrlEnum_defaultEncoding_INC : decode_BRANCH_CTRL_string = "INC ";
      `BranchCtrlEnum_defaultEncoding_B : decode_BRANCH_CTRL_string = "B   ";
      `BranchCtrlEnum_defaultEncoding_JAL : decode_BRANCH_CTRL_string = "JAL ";
      `BranchCtrlEnum_defaultEncoding_JALR : decode_BRANCH_CTRL_string = "JALR";
      default : decode_BRANCH_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_20_)
      `BranchCtrlEnum_defaultEncoding_INC : _zz_20__string = "INC ";
      `BranchCtrlEnum_defaultEncoding_B : _zz_20__string = "B   ";
      `BranchCtrlEnum_defaultEncoding_JAL : _zz_20__string = "JAL ";
      `BranchCtrlEnum_defaultEncoding_JALR : _zz_20__string = "JALR";
      default : _zz_20__string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_21_)
      `BranchCtrlEnum_defaultEncoding_INC : _zz_21__string = "INC ";
      `BranchCtrlEnum_defaultEncoding_B : _zz_21__string = "B   ";
      `BranchCtrlEnum_defaultEncoding_JAL : _zz_21__string = "JAL ";
      `BranchCtrlEnum_defaultEncoding_JALR : _zz_21__string = "JALR";
      default : _zz_21__string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_22_)
      `BranchCtrlEnum_defaultEncoding_INC : _zz_22__string = "INC ";
      `BranchCtrlEnum_defaultEncoding_B : _zz_22__string = "B   ";
      `BranchCtrlEnum_defaultEncoding_JAL : _zz_22__string = "JAL ";
      `BranchCtrlEnum_defaultEncoding_JALR : _zz_22__string = "JALR";
      default : _zz_22__string = "????";
    endcase
  end
  always @(*) begin
    case(decode_SHIFT_CTRL)
      `ShiftCtrlEnum_defaultEncoding_DISABLE_1 : decode_SHIFT_CTRL_string = "DISABLE_1";
      `ShiftCtrlEnum_defaultEncoding_SLL_1 : decode_SHIFT_CTRL_string = "SLL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRL_1 : decode_SHIFT_CTRL_string = "SRL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRA_1 : decode_SHIFT_CTRL_string = "SRA_1    ";
      default : decode_SHIFT_CTRL_string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_23_)
      `ShiftCtrlEnum_defaultEncoding_DISABLE_1 : _zz_23__string = "DISABLE_1";
      `ShiftCtrlEnum_defaultEncoding_SLL_1 : _zz_23__string = "SLL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRL_1 : _zz_23__string = "SRL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRA_1 : _zz_23__string = "SRA_1    ";
      default : _zz_23__string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_24_)
      `ShiftCtrlEnum_defaultEncoding_DISABLE_1 : _zz_24__string = "DISABLE_1";
      `ShiftCtrlEnum_defaultEncoding_SLL_1 : _zz_24__string = "SLL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRL_1 : _zz_24__string = "SRL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRA_1 : _zz_24__string = "SRA_1    ";
      default : _zz_24__string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_25_)
      `ShiftCtrlEnum_defaultEncoding_DISABLE_1 : _zz_25__string = "DISABLE_1";
      `ShiftCtrlEnum_defaultEncoding_SLL_1 : _zz_25__string = "SLL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRL_1 : _zz_25__string = "SRL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRA_1 : _zz_25__string = "SRA_1    ";
      default : _zz_25__string = "?????????";
    endcase
  end
  always @(*) begin
    case(execute_BRANCH_CTRL)
      `BranchCtrlEnum_defaultEncoding_INC : execute_BRANCH_CTRL_string = "INC ";
      `BranchCtrlEnum_defaultEncoding_B : execute_BRANCH_CTRL_string = "B   ";
      `BranchCtrlEnum_defaultEncoding_JAL : execute_BRANCH_CTRL_string = "JAL ";
      `BranchCtrlEnum_defaultEncoding_JALR : execute_BRANCH_CTRL_string = "JALR";
      default : execute_BRANCH_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_26_)
      `BranchCtrlEnum_defaultEncoding_INC : _zz_26__string = "INC ";
      `BranchCtrlEnum_defaultEncoding_B : _zz_26__string = "B   ";
      `BranchCtrlEnum_defaultEncoding_JAL : _zz_26__string = "JAL ";
      `BranchCtrlEnum_defaultEncoding_JALR : _zz_26__string = "JALR";
      default : _zz_26__string = "????";
    endcase
  end
  always @(*) begin
    case(memory_ENV_CTRL)
      `EnvCtrlEnum_defaultEncoding_NONE : memory_ENV_CTRL_string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : memory_ENV_CTRL_string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : memory_ENV_CTRL_string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : memory_ENV_CTRL_string = "ECALL";
      default : memory_ENV_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_27_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_27__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_27__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_27__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_27__string = "ECALL";
      default : _zz_27__string = "?????";
    endcase
  end
  always @(*) begin
    case(execute_ENV_CTRL)
      `EnvCtrlEnum_defaultEncoding_NONE : execute_ENV_CTRL_string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : execute_ENV_CTRL_string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : execute_ENV_CTRL_string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : execute_ENV_CTRL_string = "ECALL";
      default : execute_ENV_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_28_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_28__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_28__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_28__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_28__string = "ECALL";
      default : _zz_28__string = "?????";
    endcase
  end
  always @(*) begin
    case(writeBack_ENV_CTRL)
      `EnvCtrlEnum_defaultEncoding_NONE : writeBack_ENV_CTRL_string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : writeBack_ENV_CTRL_string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : writeBack_ENV_CTRL_string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : writeBack_ENV_CTRL_string = "ECALL";
      default : writeBack_ENV_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_29_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_29__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_29__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_29__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_29__string = "ECALL";
      default : _zz_29__string = "?????";
    endcase
  end
  always @(*) begin
    case(execute_SHIFT_CTRL)
      `ShiftCtrlEnum_defaultEncoding_DISABLE_1 : execute_SHIFT_CTRL_string = "DISABLE_1";
      `ShiftCtrlEnum_defaultEncoding_SLL_1 : execute_SHIFT_CTRL_string = "SLL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRL_1 : execute_SHIFT_CTRL_string = "SRL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRA_1 : execute_SHIFT_CTRL_string = "SRA_1    ";
      default : execute_SHIFT_CTRL_string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_32_)
      `ShiftCtrlEnum_defaultEncoding_DISABLE_1 : _zz_32__string = "DISABLE_1";
      `ShiftCtrlEnum_defaultEncoding_SLL_1 : _zz_32__string = "SLL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRL_1 : _zz_32__string = "SRL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRA_1 : _zz_32__string = "SRA_1    ";
      default : _zz_32__string = "?????????";
    endcase
  end
  always @(*) begin
    case(execute_SRC2_CTRL)
      `Src2CtrlEnum_defaultEncoding_RS : execute_SRC2_CTRL_string = "RS ";
      `Src2CtrlEnum_defaultEncoding_IMI : execute_SRC2_CTRL_string = "IMI";
      `Src2CtrlEnum_defaultEncoding_IMS : execute_SRC2_CTRL_string = "IMS";
      `Src2CtrlEnum_defaultEncoding_PC : execute_SRC2_CTRL_string = "PC ";
      default : execute_SRC2_CTRL_string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_34_)
      `Src2CtrlEnum_defaultEncoding_RS : _zz_34__string = "RS ";
      `Src2CtrlEnum_defaultEncoding_IMI : _zz_34__string = "IMI";
      `Src2CtrlEnum_defaultEncoding_IMS : _zz_34__string = "IMS";
      `Src2CtrlEnum_defaultEncoding_PC : _zz_34__string = "PC ";
      default : _zz_34__string = "???";
    endcase
  end
  always @(*) begin
    case(execute_SRC1_CTRL)
      `Src1CtrlEnum_defaultEncoding_RS : execute_SRC1_CTRL_string = "RS          ";
      `Src1CtrlEnum_defaultEncoding_IMU : execute_SRC1_CTRL_string = "IMU         ";
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : execute_SRC1_CTRL_string = "PC_INCREMENT";
      `Src1CtrlEnum_defaultEncoding_URS1 : execute_SRC1_CTRL_string = "URS1        ";
      default : execute_SRC1_CTRL_string = "????????????";
    endcase
  end
  always @(*) begin
    case(_zz_35_)
      `Src1CtrlEnum_defaultEncoding_RS : _zz_35__string = "RS          ";
      `Src1CtrlEnum_defaultEncoding_IMU : _zz_35__string = "IMU         ";
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : _zz_35__string = "PC_INCREMENT";
      `Src1CtrlEnum_defaultEncoding_URS1 : _zz_35__string = "URS1        ";
      default : _zz_35__string = "????????????";
    endcase
  end
  always @(*) begin
    case(execute_ALU_CTRL)
      `AluCtrlEnum_defaultEncoding_ADD_SUB : execute_ALU_CTRL_string = "ADD_SUB ";
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : execute_ALU_CTRL_string = "SLT_SLTU";
      `AluCtrlEnum_defaultEncoding_BITWISE : execute_ALU_CTRL_string = "BITWISE ";
      default : execute_ALU_CTRL_string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_36_)
      `AluCtrlEnum_defaultEncoding_ADD_SUB : _zz_36__string = "ADD_SUB ";
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : _zz_36__string = "SLT_SLTU";
      `AluCtrlEnum_defaultEncoding_BITWISE : _zz_36__string = "BITWISE ";
      default : _zz_36__string = "????????";
    endcase
  end
  always @(*) begin
    case(execute_ALU_BITWISE_CTRL)
      `AluBitwiseCtrlEnum_defaultEncoding_XOR_1 : execute_ALU_BITWISE_CTRL_string = "XOR_1";
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : execute_ALU_BITWISE_CTRL_string = "OR_1 ";
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : execute_ALU_BITWISE_CTRL_string = "AND_1";
      default : execute_ALU_BITWISE_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_37_)
      `AluBitwiseCtrlEnum_defaultEncoding_XOR_1 : _zz_37__string = "XOR_1";
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : _zz_37__string = "OR_1 ";
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : _zz_37__string = "AND_1";
      default : _zz_37__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_41_)
      `ShiftCtrlEnum_defaultEncoding_DISABLE_1 : _zz_41__string = "DISABLE_1";
      `ShiftCtrlEnum_defaultEncoding_SLL_1 : _zz_41__string = "SLL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRL_1 : _zz_41__string = "SRL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRA_1 : _zz_41__string = "SRA_1    ";
      default : _zz_41__string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_42_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_42__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_42__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_42__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_42__string = "ECALL";
      default : _zz_42__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_43_)
      `AluCtrlEnum_defaultEncoding_ADD_SUB : _zz_43__string = "ADD_SUB ";
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : _zz_43__string = "SLT_SLTU";
      `AluCtrlEnum_defaultEncoding_BITWISE : _zz_43__string = "BITWISE ";
      default : _zz_43__string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_44_)
      `Src2CtrlEnum_defaultEncoding_RS : _zz_44__string = "RS ";
      `Src2CtrlEnum_defaultEncoding_IMI : _zz_44__string = "IMI";
      `Src2CtrlEnum_defaultEncoding_IMS : _zz_44__string = "IMS";
      `Src2CtrlEnum_defaultEncoding_PC : _zz_44__string = "PC ";
      default : _zz_44__string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_45_)
      `AluBitwiseCtrlEnum_defaultEncoding_XOR_1 : _zz_45__string = "XOR_1";
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : _zz_45__string = "OR_1 ";
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : _zz_45__string = "AND_1";
      default : _zz_45__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_46_)
      `Src1CtrlEnum_defaultEncoding_RS : _zz_46__string = "RS          ";
      `Src1CtrlEnum_defaultEncoding_IMU : _zz_46__string = "IMU         ";
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : _zz_46__string = "PC_INCREMENT";
      `Src1CtrlEnum_defaultEncoding_URS1 : _zz_46__string = "URS1        ";
      default : _zz_46__string = "????????????";
    endcase
  end
  always @(*) begin
    case(_zz_47_)
      `BranchCtrlEnum_defaultEncoding_INC : _zz_47__string = "INC ";
      `BranchCtrlEnum_defaultEncoding_B : _zz_47__string = "B   ";
      `BranchCtrlEnum_defaultEncoding_JAL : _zz_47__string = "JAL ";
      `BranchCtrlEnum_defaultEncoding_JALR : _zz_47__string = "JALR";
      default : _zz_47__string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_80_)
      `BranchCtrlEnum_defaultEncoding_INC : _zz_80__string = "INC ";
      `BranchCtrlEnum_defaultEncoding_B : _zz_80__string = "B   ";
      `BranchCtrlEnum_defaultEncoding_JAL : _zz_80__string = "JAL ";
      `BranchCtrlEnum_defaultEncoding_JALR : _zz_80__string = "JALR";
      default : _zz_80__string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_81_)
      `Src1CtrlEnum_defaultEncoding_RS : _zz_81__string = "RS          ";
      `Src1CtrlEnum_defaultEncoding_IMU : _zz_81__string = "IMU         ";
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : _zz_81__string = "PC_INCREMENT";
      `Src1CtrlEnum_defaultEncoding_URS1 : _zz_81__string = "URS1        ";
      default : _zz_81__string = "????????????";
    endcase
  end
  always @(*) begin
    case(_zz_82_)
      `AluBitwiseCtrlEnum_defaultEncoding_XOR_1 : _zz_82__string = "XOR_1";
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : _zz_82__string = "OR_1 ";
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : _zz_82__string = "AND_1";
      default : _zz_82__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_83_)
      `Src2CtrlEnum_defaultEncoding_RS : _zz_83__string = "RS ";
      `Src2CtrlEnum_defaultEncoding_IMI : _zz_83__string = "IMI";
      `Src2CtrlEnum_defaultEncoding_IMS : _zz_83__string = "IMS";
      `Src2CtrlEnum_defaultEncoding_PC : _zz_83__string = "PC ";
      default : _zz_83__string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_84_)
      `AluCtrlEnum_defaultEncoding_ADD_SUB : _zz_84__string = "ADD_SUB ";
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : _zz_84__string = "SLT_SLTU";
      `AluCtrlEnum_defaultEncoding_BITWISE : _zz_84__string = "BITWISE ";
      default : _zz_84__string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_85_)
      `EnvCtrlEnum_defaultEncoding_NONE : _zz_85__string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : _zz_85__string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : _zz_85__string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : _zz_85__string = "ECALL";
      default : _zz_85__string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_86_)
      `ShiftCtrlEnum_defaultEncoding_DISABLE_1 : _zz_86__string = "DISABLE_1";
      `ShiftCtrlEnum_defaultEncoding_SLL_1 : _zz_86__string = "SLL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRL_1 : _zz_86__string = "SRL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRA_1 : _zz_86__string = "SRA_1    ";
      default : _zz_86__string = "?????????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_SHIFT_CTRL)
      `ShiftCtrlEnum_defaultEncoding_DISABLE_1 : decode_to_execute_SHIFT_CTRL_string = "DISABLE_1";
      `ShiftCtrlEnum_defaultEncoding_SLL_1 : decode_to_execute_SHIFT_CTRL_string = "SLL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRL_1 : decode_to_execute_SHIFT_CTRL_string = "SRL_1    ";
      `ShiftCtrlEnum_defaultEncoding_SRA_1 : decode_to_execute_SHIFT_CTRL_string = "SRA_1    ";
      default : decode_to_execute_SHIFT_CTRL_string = "?????????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_BRANCH_CTRL)
      `BranchCtrlEnum_defaultEncoding_INC : decode_to_execute_BRANCH_CTRL_string = "INC ";
      `BranchCtrlEnum_defaultEncoding_B : decode_to_execute_BRANCH_CTRL_string = "B   ";
      `BranchCtrlEnum_defaultEncoding_JAL : decode_to_execute_BRANCH_CTRL_string = "JAL ";
      `BranchCtrlEnum_defaultEncoding_JALR : decode_to_execute_BRANCH_CTRL_string = "JALR";
      default : decode_to_execute_BRANCH_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_SRC1_CTRL)
      `Src1CtrlEnum_defaultEncoding_RS : decode_to_execute_SRC1_CTRL_string = "RS          ";
      `Src1CtrlEnum_defaultEncoding_IMU : decode_to_execute_SRC1_CTRL_string = "IMU         ";
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : decode_to_execute_SRC1_CTRL_string = "PC_INCREMENT";
      `Src1CtrlEnum_defaultEncoding_URS1 : decode_to_execute_SRC1_CTRL_string = "URS1        ";
      default : decode_to_execute_SRC1_CTRL_string = "????????????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_ENV_CTRL)
      `EnvCtrlEnum_defaultEncoding_NONE : decode_to_execute_ENV_CTRL_string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : decode_to_execute_ENV_CTRL_string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : decode_to_execute_ENV_CTRL_string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : decode_to_execute_ENV_CTRL_string = "ECALL";
      default : decode_to_execute_ENV_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(execute_to_memory_ENV_CTRL)
      `EnvCtrlEnum_defaultEncoding_NONE : execute_to_memory_ENV_CTRL_string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : execute_to_memory_ENV_CTRL_string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : execute_to_memory_ENV_CTRL_string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : execute_to_memory_ENV_CTRL_string = "ECALL";
      default : execute_to_memory_ENV_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(memory_to_writeBack_ENV_CTRL)
      `EnvCtrlEnum_defaultEncoding_NONE : memory_to_writeBack_ENV_CTRL_string = "NONE ";
      `EnvCtrlEnum_defaultEncoding_XRET : memory_to_writeBack_ENV_CTRL_string = "XRET ";
      `EnvCtrlEnum_defaultEncoding_WFI : memory_to_writeBack_ENV_CTRL_string = "WFI  ";
      `EnvCtrlEnum_defaultEncoding_ECALL : memory_to_writeBack_ENV_CTRL_string = "ECALL";
      default : memory_to_writeBack_ENV_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_ALU_CTRL)
      `AluCtrlEnum_defaultEncoding_ADD_SUB : decode_to_execute_ALU_CTRL_string = "ADD_SUB ";
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : decode_to_execute_ALU_CTRL_string = "SLT_SLTU";
      `AluCtrlEnum_defaultEncoding_BITWISE : decode_to_execute_ALU_CTRL_string = "BITWISE ";
      default : decode_to_execute_ALU_CTRL_string = "????????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_SRC2_CTRL)
      `Src2CtrlEnum_defaultEncoding_RS : decode_to_execute_SRC2_CTRL_string = "RS ";
      `Src2CtrlEnum_defaultEncoding_IMI : decode_to_execute_SRC2_CTRL_string = "IMI";
      `Src2CtrlEnum_defaultEncoding_IMS : decode_to_execute_SRC2_CTRL_string = "IMS";
      `Src2CtrlEnum_defaultEncoding_PC : decode_to_execute_SRC2_CTRL_string = "PC ";
      default : decode_to_execute_SRC2_CTRL_string = "???";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_ALU_BITWISE_CTRL)
      `AluBitwiseCtrlEnum_defaultEncoding_XOR_1 : decode_to_execute_ALU_BITWISE_CTRL_string = "XOR_1";
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : decode_to_execute_ALU_BITWISE_CTRL_string = "OR_1 ";
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : decode_to_execute_ALU_BITWISE_CTRL_string = "AND_1";
      default : decode_to_execute_ALU_BITWISE_CTRL_string = "?????";
    endcase
  end
  `endif

  assign decode_ALU_BITWISE_CTRL = _zz_1_;
  assign _zz_2_ = _zz_3_;
  assign decode_SRC2_CTRL = _zz_4_;
  assign _zz_5_ = _zz_6_;
  assign memory_PC = execute_to_memory_PC;
  assign memory_IS_MUL = execute_to_memory_IS_MUL;
  assign execute_IS_MUL = decode_to_execute_IS_MUL;
  assign decode_IS_MUL = _zz_182_[0];
  assign memory_MUL_HH = execute_to_memory_MUL_HH;
  assign execute_MUL_HH = ($signed(execute_MulPlugin_aHigh) * $signed(execute_MulPlugin_bHigh));
  assign execute_BYPASSABLE_MEMORY_STAGE = decode_to_execute_BYPASSABLE_MEMORY_STAGE;
  assign decode_BYPASSABLE_MEMORY_STAGE = _zz_183_[0];
  assign decode_CSR_WRITE_OPCODE = (! (((decode_INSTRUCTION[14 : 13] == (2'b01)) && (decode_INSTRUCTION[19 : 15] == 5'h0)) || ((decode_INSTRUCTION[14 : 13] == (2'b11)) && (decode_INSTRUCTION[19 : 15] == 5'h0))));
  assign decode_SRC_LESS_UNSIGNED = _zz_184_[0];
  assign memory_MUL_LOW = ($signed(_zz_185_) + $signed(_zz_193_));
  assign writeBack_FORMAL_PC_NEXT = memory_to_writeBack_FORMAL_PC_NEXT;
  assign memory_FORMAL_PC_NEXT = execute_to_memory_FORMAL_PC_NEXT;
  assign execute_FORMAL_PC_NEXT = decode_to_execute_FORMAL_PC_NEXT;
  assign decode_FORMAL_PC_NEXT = (decode_PC + 32'h00000004);
  assign decode_IS_DIV = _zz_194_[0];
  assign decode_IS_CSR = _zz_195_[0];
  assign execute_MUL_LH = ($signed(execute_MulPlugin_aSLow) * $signed(execute_MulPlugin_bHigh));
  assign decode_MEMORY_STORE = _zz_196_[0];
  assign decode_SRC2_FORCE_ZERO = (decode_SRC_ADD_ZERO && (! decode_SRC_USE_SUB_LESS));
  assign decode_DO_EBREAK = ((! DebugPlugin_haltIt) && (decode_IS_EBREAK || 1'b0));
  assign memory_MEMORY_ADDRESS_LOW = execute_to_memory_MEMORY_ADDRESS_LOW;
  assign execute_MEMORY_ADDRESS_LOW = dBus_cmd_payload_address[1 : 0];
  assign memory_MEMORY_READ_DATA = dBus_rsp_data;
  assign decode_ALU_CTRL = _zz_7_;
  assign _zz_8_ = _zz_9_;
  assign _zz_10_ = _zz_11_;
  assign _zz_12_ = _zz_13_;
  assign decode_ENV_CTRL = _zz_14_;
  assign _zz_15_ = _zz_16_;
  assign decode_MEMORY_ENABLE = _zz_197_[0];
  assign decode_IS_RS1_SIGNED = _zz_198_[0];
  assign execute_MUL_HL = ($signed(execute_MulPlugin_aHigh) * $signed(execute_MulPlugin_bSLow));
  assign decode_BYPASSABLE_EXECUTE_STAGE = _zz_199_[0];
  assign decode_SRC1_CTRL = _zz_17_;
  assign _zz_18_ = _zz_19_;
  assign decode_CSR_READ_OPCODE = (decode_INSTRUCTION[13 : 7] != 7'h20);
  assign decode_IS_RS2_SIGNED = _zz_200_[0];
  assign decode_BRANCH_CTRL = _zz_20_;
  assign _zz_21_ = _zz_22_;
  assign execute_MUL_LL = (execute_MulPlugin_aULow * execute_MulPlugin_bULow);
  assign decode_SHIFT_CTRL = _zz_23_;
  assign _zz_24_ = _zz_25_;
  assign writeBack_REGFILE_WRITE_DATA = memory_to_writeBack_REGFILE_WRITE_DATA;
  assign memory_REGFILE_WRITE_DATA = execute_to_memory_REGFILE_WRITE_DATA;
  assign execute_REGFILE_WRITE_DATA = _zz_88_;
  assign execute_DO_EBREAK = decode_to_execute_DO_EBREAK;
  assign decode_IS_EBREAK = _zz_201_[0];
  assign execute_BRANCH_CALC = {execute_BranchPlugin_branchAdder[31 : 1],(1'b0)};
  assign execute_BRANCH_DO = _zz_120_;
  assign execute_PC = decode_to_execute_PC;
  assign execute_BRANCH_CTRL = _zz_26_;
  assign execute_CSR_READ_OPCODE = decode_to_execute_CSR_READ_OPCODE;
  assign execute_CSR_WRITE_OPCODE = decode_to_execute_CSR_WRITE_OPCODE;
  assign execute_IS_CSR = decode_to_execute_IS_CSR;
  assign memory_ENV_CTRL = _zz_27_;
  assign execute_ENV_CTRL = _zz_28_;
  assign writeBack_ENV_CTRL = _zz_29_;
  assign execute_IS_RS1_SIGNED = decode_to_execute_IS_RS1_SIGNED;
  assign execute_IS_DIV = decode_to_execute_IS_DIV;
  assign execute_IS_RS2_SIGNED = decode_to_execute_IS_RS2_SIGNED;
  assign memory_IS_DIV = execute_to_memory_IS_DIV;
  assign writeBack_IS_MUL = memory_to_writeBack_IS_MUL;
  assign writeBack_MUL_HH = memory_to_writeBack_MUL_HH;
  assign writeBack_MUL_LOW = memory_to_writeBack_MUL_LOW;
  assign memory_MUL_HL = execute_to_memory_MUL_HL;
  assign memory_MUL_LH = execute_to_memory_MUL_LH;
  assign memory_MUL_LL = execute_to_memory_MUL_LL;
  assign execute_RS1 = decode_to_execute_RS1;
  assign decode_RS2_USE = _zz_202_[0];
  assign decode_RS1_USE = _zz_203_[0];
  assign execute_REGFILE_WRITE_VALID = decode_to_execute_REGFILE_WRITE_VALID;
  assign execute_BYPASSABLE_EXECUTE_STAGE = decode_to_execute_BYPASSABLE_EXECUTE_STAGE;
  always @ (*) begin
    _zz_30_ = memory_REGFILE_WRITE_DATA;
    if(_zz_149_)begin
      _zz_30_ = memory_DivPlugin_div_result;
    end
  end

  assign memory_REGFILE_WRITE_VALID = execute_to_memory_REGFILE_WRITE_VALID;
  assign memory_INSTRUCTION = execute_to_memory_INSTRUCTION;
  assign memory_BYPASSABLE_MEMORY_STAGE = execute_to_memory_BYPASSABLE_MEMORY_STAGE;
  assign writeBack_REGFILE_WRITE_VALID = memory_to_writeBack_REGFILE_WRITE_VALID;
  always @ (*) begin
    decode_RS2 = decode_RegFilePlugin_rs2Data;
    if(_zz_99_)begin
      if((_zz_100_ == decode_INSTRUCTION[24 : 20]))begin
        decode_RS2 = _zz_101_;
      end
    end
    if(_zz_150_)begin
      if(_zz_151_)begin
        if(_zz_103_)begin
          decode_RS2 = _zz_48_;
        end
      end
    end
    if(_zz_152_)begin
      if(memory_BYPASSABLE_MEMORY_STAGE)begin
        if(_zz_105_)begin
          decode_RS2 = _zz_30_;
        end
      end
    end
    if(_zz_153_)begin
      if(execute_BYPASSABLE_EXECUTE_STAGE)begin
        if(_zz_107_)begin
          decode_RS2 = _zz_31_;
        end
      end
    end
  end

  always @ (*) begin
    decode_RS1 = decode_RegFilePlugin_rs1Data;
    if(_zz_99_)begin
      if((_zz_100_ == decode_INSTRUCTION[19 : 15]))begin
        decode_RS1 = _zz_101_;
      end
    end
    if(_zz_150_)begin
      if(_zz_151_)begin
        if(_zz_102_)begin
          decode_RS1 = _zz_48_;
        end
      end
    end
    if(_zz_152_)begin
      if(memory_BYPASSABLE_MEMORY_STAGE)begin
        if(_zz_104_)begin
          decode_RS1 = _zz_30_;
        end
      end
    end
    if(_zz_153_)begin
      if(execute_BYPASSABLE_EXECUTE_STAGE)begin
        if(_zz_106_)begin
          decode_RS1 = _zz_31_;
        end
      end
    end
  end

  assign execute_SHIFT_RIGHT = _zz_205_;
  always @ (*) begin
    _zz_31_ = execute_REGFILE_WRITE_DATA;
    if(execute_arbitration_isValid)begin
      case(execute_SHIFT_CTRL)
        `ShiftCtrlEnum_defaultEncoding_SLL_1 : begin
          _zz_31_ = _zz_96_;
        end
        `ShiftCtrlEnum_defaultEncoding_SRL_1, `ShiftCtrlEnum_defaultEncoding_SRA_1 : begin
          _zz_31_ = execute_SHIFT_RIGHT;
        end
        default : begin
        end
      endcase
    end
    if(_zz_154_)begin
      _zz_31_ = execute_CsrPlugin_readData;
    end
  end

  assign execute_SHIFT_CTRL = _zz_32_;
  assign execute_SRC_LESS_UNSIGNED = decode_to_execute_SRC_LESS_UNSIGNED;
  assign execute_SRC2_FORCE_ZERO = decode_to_execute_SRC2_FORCE_ZERO;
  assign execute_SRC_USE_SUB_LESS = decode_to_execute_SRC_USE_SUB_LESS;
  assign _zz_33_ = execute_PC;
  assign execute_SRC2_CTRL = _zz_34_;
  assign execute_SRC1_CTRL = _zz_35_;
  assign decode_SRC_USE_SUB_LESS = _zz_207_[0];
  assign decode_SRC_ADD_ZERO = _zz_208_[0];
  assign execute_SRC_ADD_SUB = execute_SrcPlugin_addSub;
  assign execute_SRC_LESS = execute_SrcPlugin_less;
  assign execute_ALU_CTRL = _zz_36_;
  assign execute_SRC2 = _zz_94_;
  assign execute_SRC1 = _zz_89_;
  assign execute_ALU_BITWISE_CTRL = _zz_37_;
  assign _zz_38_ = writeBack_INSTRUCTION;
  assign _zz_39_ = writeBack_REGFILE_WRITE_VALID;
  always @ (*) begin
    _zz_40_ = 1'b0;
    if(lastStageRegFileWrite_valid)begin
      _zz_40_ = 1'b1;
    end
  end

  always @ (*) begin
    decode_REGFILE_WRITE_VALID = _zz_209_[0];
    if((decode_INSTRUCTION[11 : 7] == 5'h0))begin
      decode_REGFILE_WRITE_VALID = 1'b0;
    end
  end

  assign decode_LEGAL_INSTRUCTION = ({((decode_INSTRUCTION & 32'h0000005f) == 32'h00000017),{((decode_INSTRUCTION & 32'h0000007f) == 32'h0000006f),{((decode_INSTRUCTION & 32'h0000106f) == 32'h00000003),{((decode_INSTRUCTION & _zz_264_) == 32'h00001073),{(_zz_265_ == _zz_266_),{_zz_267_,{_zz_268_,_zz_269_}}}}}}} != 20'h0);
  assign writeBack_MEMORY_STORE = memory_to_writeBack_MEMORY_STORE;
  always @ (*) begin
    _zz_48_ = writeBack_REGFILE_WRITE_DATA;
    if((writeBack_arbitration_isValid && writeBack_MEMORY_ENABLE))begin
      _zz_48_ = writeBack_DBusSimplePlugin_rspFormated;
    end
    if((writeBack_arbitration_isValid && writeBack_IS_MUL))begin
      case(_zz_180_)
        2'b00 : begin
          _zz_48_ = _zz_235_;
        end
        default : begin
          _zz_48_ = _zz_236_;
        end
      endcase
    end
  end

  assign writeBack_MEMORY_ENABLE = memory_to_writeBack_MEMORY_ENABLE;
  assign writeBack_MEMORY_ADDRESS_LOW = memory_to_writeBack_MEMORY_ADDRESS_LOW;
  assign writeBack_MEMORY_READ_DATA = memory_to_writeBack_MEMORY_READ_DATA;
  assign memory_MEMORY_STORE = execute_to_memory_MEMORY_STORE;
  assign memory_MEMORY_ENABLE = execute_to_memory_MEMORY_ENABLE;
  assign execute_SRC_ADD = execute_SrcPlugin_addSub;
  assign execute_RS2 = decode_to_execute_RS2;
  assign execute_INSTRUCTION = decode_to_execute_INSTRUCTION;
  assign execute_MEMORY_STORE = decode_to_execute_MEMORY_STORE;
  assign execute_MEMORY_ENABLE = decode_to_execute_MEMORY_ENABLE;
  assign execute_ALIGNEMENT_FAULT = 1'b0;
  always @ (*) begin
    _zz_49_ = execute_FORMAL_PC_NEXT;
    if(BranchPlugin_jumpInterface_valid)begin
      _zz_49_ = BranchPlugin_jumpInterface_payload;
    end
  end

  assign decode_PC = IBusSimplePlugin_injector_decodeInput_payload_pc;
  assign decode_INSTRUCTION = IBusSimplePlugin_injector_decodeInput_payload_rsp_inst;
  assign writeBack_PC = memory_to_writeBack_PC;
  assign writeBack_INSTRUCTION = memory_to_writeBack_INSTRUCTION;
  always @ (*) begin
    decode_arbitration_haltItself = 1'b0;
    case(_zz_129_)
      3'b000 : begin
      end
      3'b001 : begin
      end
      3'b010 : begin
        decode_arbitration_haltItself = 1'b1;
      end
      3'b011 : begin
      end
      3'b100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    decode_arbitration_haltByOther = 1'b0;
    if((decode_arbitration_isValid && (_zz_97_ || _zz_98_)))begin
      decode_arbitration_haltByOther = 1'b1;
    end
    if(CsrPlugin_pipelineLiberator_active)begin
      decode_arbitration_haltByOther = 1'b1;
    end
    if(({(writeBack_arbitration_isValid && (writeBack_ENV_CTRL == `EnvCtrlEnum_defaultEncoding_XRET)),{(memory_arbitration_isValid && (memory_ENV_CTRL == `EnvCtrlEnum_defaultEncoding_XRET)),(execute_arbitration_isValid && (execute_ENV_CTRL == `EnvCtrlEnum_defaultEncoding_XRET))}} != (3'b000)))begin
      decode_arbitration_haltByOther = 1'b1;
    end
  end

  always @ (*) begin
    decode_arbitration_removeIt = 1'b0;
    if(decodeExceptionPort_valid)begin
      decode_arbitration_removeIt = 1'b1;
    end
    if(decode_arbitration_isFlushed)begin
      decode_arbitration_removeIt = 1'b1;
    end
  end

  assign decode_arbitration_flushIt = 1'b0;
  always @ (*) begin
    decode_arbitration_flushNext = 1'b0;
    if(decodeExceptionPort_valid)begin
      decode_arbitration_flushNext = 1'b1;
    end
  end

  always @ (*) begin
    execute_arbitration_haltItself = 1'b0;
    if(((((execute_arbitration_isValid && execute_MEMORY_ENABLE) && (! dBus_cmd_ready)) && (! execute_DBusSimplePlugin_skipCmd)) && (! _zz_66_)))begin
      execute_arbitration_haltItself = 1'b1;
    end
    if(_zz_155_)begin
      if((! execute_CsrPlugin_wfiWake))begin
        execute_arbitration_haltItself = 1'b1;
      end
    end
    if(_zz_154_)begin
      if(execute_CsrPlugin_blockedBySideEffects)begin
        execute_arbitration_haltItself = 1'b1;
      end
    end
  end

  always @ (*) begin
    execute_arbitration_haltByOther = 1'b0;
    if(_zz_156_)begin
      execute_arbitration_haltByOther = 1'b1;
    end
  end

  always @ (*) begin
    execute_arbitration_removeIt = 1'b0;
    if(_zz_157_)begin
      execute_arbitration_removeIt = 1'b1;
    end
    if(execute_arbitration_isFlushed)begin
      execute_arbitration_removeIt = 1'b1;
    end
  end

  always @ (*) begin
    execute_arbitration_flushIt = 1'b0;
    if(_zz_156_)begin
      if(_zz_158_)begin
        execute_arbitration_flushIt = 1'b1;
      end
    end
  end

  always @ (*) begin
    execute_arbitration_flushNext = 1'b0;
    if(_zz_157_)begin
      execute_arbitration_flushNext = 1'b1;
    end
    if(BranchPlugin_jumpInterface_valid)begin
      execute_arbitration_flushNext = 1'b1;
    end
    if(_zz_156_)begin
      if(_zz_158_)begin
        execute_arbitration_flushNext = 1'b1;
      end
    end
  end

  always @ (*) begin
    memory_arbitration_haltItself = 1'b0;
    if((((memory_arbitration_isValid && memory_MEMORY_ENABLE) && (! memory_MEMORY_STORE)) && ((! dBus_rsp_ready) || 1'b0)))begin
      memory_arbitration_haltItself = 1'b1;
    end
    if(_zz_149_)begin
      if(((! memory_DivPlugin_frontendOk) || (! memory_DivPlugin_div_done)))begin
        memory_arbitration_haltItself = 1'b1;
      end
    end
  end

  assign memory_arbitration_haltByOther = 1'b0;
  always @ (*) begin
    memory_arbitration_removeIt = 1'b0;
    if(memory_arbitration_isFlushed)begin
      memory_arbitration_removeIt = 1'b1;
    end
  end

  assign memory_arbitration_flushIt = 1'b0;
  assign memory_arbitration_flushNext = 1'b0;
  assign writeBack_arbitration_haltItself = 1'b0;
  assign writeBack_arbitration_haltByOther = 1'b0;
  always @ (*) begin
    writeBack_arbitration_removeIt = 1'b0;
    if(writeBack_arbitration_isFlushed)begin
      writeBack_arbitration_removeIt = 1'b1;
    end
  end

  assign writeBack_arbitration_flushIt = 1'b0;
  always @ (*) begin
    writeBack_arbitration_flushNext = 1'b0;
    if(_zz_159_)begin
      writeBack_arbitration_flushNext = 1'b1;
    end
    if(_zz_160_)begin
      writeBack_arbitration_flushNext = 1'b1;
    end
  end

  assign lastStageInstruction = writeBack_INSTRUCTION;
  assign lastStagePc = writeBack_PC;
  assign lastStageIsValid = writeBack_arbitration_isValid;
  assign lastStageIsFiring = writeBack_arbitration_isFiring;
  always @ (*) begin
    IBusSimplePlugin_fetcherHalt = 1'b0;
    if(({CsrPlugin_exceptionPortCtrl_exceptionValids_writeBack,{CsrPlugin_exceptionPortCtrl_exceptionValids_memory,{CsrPlugin_exceptionPortCtrl_exceptionValids_execute,CsrPlugin_exceptionPortCtrl_exceptionValids_decode}}} != (4'b0000)))begin
      IBusSimplePlugin_fetcherHalt = 1'b1;
    end
    if(_zz_159_)begin
      IBusSimplePlugin_fetcherHalt = 1'b1;
    end
    if(_zz_160_)begin
      IBusSimplePlugin_fetcherHalt = 1'b1;
    end
    if(_zz_156_)begin
      if(_zz_158_)begin
        IBusSimplePlugin_fetcherHalt = 1'b1;
      end
    end
    if(DebugPlugin_haltIt)begin
      IBusSimplePlugin_fetcherHalt = 1'b1;
    end
    if(_zz_161_)begin
      IBusSimplePlugin_fetcherHalt = 1'b1;
    end
  end

  always @ (*) begin
    IBusSimplePlugin_incomingInstruction = 1'b0;
    if((IBusSimplePlugin_iBusRsp_stages_1_input_valid || IBusSimplePlugin_iBusRsp_stages_2_input_valid))begin
      IBusSimplePlugin_incomingInstruction = 1'b1;
    end
    if(IBusSimplePlugin_injector_decodeInput_valid)begin
      IBusSimplePlugin_incomingInstruction = 1'b1;
    end
  end

  always @ (*) begin
    CsrPlugin_inWfi = 1'b0;
    if(_zz_155_)begin
      CsrPlugin_inWfi = 1'b1;
    end
  end

  always @ (*) begin
    CsrPlugin_thirdPartyWake = 1'b0;
    if(DebugPlugin_haltIt)begin
      CsrPlugin_thirdPartyWake = 1'b1;
    end
  end

  always @ (*) begin
    CsrPlugin_jumpInterface_valid = 1'b0;
    if(_zz_159_)begin
      CsrPlugin_jumpInterface_valid = 1'b1;
    end
    if(_zz_160_)begin
      CsrPlugin_jumpInterface_valid = 1'b1;
    end
  end

  always @ (*) begin
    CsrPlugin_jumpInterface_payload = 32'h0;
    if(_zz_159_)begin
      CsrPlugin_jumpInterface_payload = {CsrPlugin_xtvec_base,(2'b00)};
    end
    if(_zz_160_)begin
      case(_zz_162_)
        2'b11 : begin
          CsrPlugin_jumpInterface_payload = CsrPlugin_mepc;
        end
        default : begin
        end
      endcase
    end
  end

  always @ (*) begin
    CsrPlugin_forceMachineWire = 1'b0;
    if(DebugPlugin_godmode)begin
      CsrPlugin_forceMachineWire = 1'b1;
    end
  end

  always @ (*) begin
    CsrPlugin_allowInterrupts = 1'b1;
    if((DebugPlugin_haltIt || DebugPlugin_stepIt))begin
      CsrPlugin_allowInterrupts = 1'b0;
    end
  end

  always @ (*) begin
    CsrPlugin_allowException = 1'b1;
    if(DebugPlugin_godmode)begin
      CsrPlugin_allowException = 1'b0;
    end
  end

  assign IBusSimplePlugin_externalFlush = ({writeBack_arbitration_flushNext,{memory_arbitration_flushNext,{execute_arbitration_flushNext,decode_arbitration_flushNext}}} != (4'b0000));
  assign IBusSimplePlugin_jump_pcLoad_valid = ({BranchPlugin_jumpInterface_valid,CsrPlugin_jumpInterface_valid} != (2'b00));
  assign _zz_50_ = {BranchPlugin_jumpInterface_valid,CsrPlugin_jumpInterface_valid};
  assign IBusSimplePlugin_jump_pcLoad_payload = (_zz_210_[0] ? CsrPlugin_jumpInterface_payload : BranchPlugin_jumpInterface_payload);
  always @ (*) begin
    IBusSimplePlugin_fetchPc_correction = 1'b0;
    if(IBusSimplePlugin_jump_pcLoad_valid)begin
      IBusSimplePlugin_fetchPc_correction = 1'b1;
    end
  end

  assign IBusSimplePlugin_fetchPc_corrected = (IBusSimplePlugin_fetchPc_correction || IBusSimplePlugin_fetchPc_correctionReg);
  assign IBusSimplePlugin_fetchPc_pcRegPropagate = 1'b0;
  always @ (*) begin
    IBusSimplePlugin_fetchPc_pc = (IBusSimplePlugin_fetchPc_pcReg + _zz_213_);
    if(IBusSimplePlugin_jump_pcLoad_valid)begin
      IBusSimplePlugin_fetchPc_pc = IBusSimplePlugin_jump_pcLoad_payload;
    end
    IBusSimplePlugin_fetchPc_pc[0] = 1'b0;
    IBusSimplePlugin_fetchPc_pc[1] = 1'b0;
  end

  always @ (*) begin
    IBusSimplePlugin_fetchPc_flushed = 1'b0;
    if(IBusSimplePlugin_jump_pcLoad_valid)begin
      IBusSimplePlugin_fetchPc_flushed = 1'b1;
    end
  end

  assign IBusSimplePlugin_fetchPc_output_valid = ((! IBusSimplePlugin_fetcherHalt) && IBusSimplePlugin_fetchPc_booted);
  assign IBusSimplePlugin_fetchPc_output_payload = IBusSimplePlugin_fetchPc_pc;
  assign IBusSimplePlugin_iBusRsp_redoFetch = 1'b0;
  assign IBusSimplePlugin_iBusRsp_stages_0_input_valid = IBusSimplePlugin_fetchPc_output_valid;
  assign IBusSimplePlugin_fetchPc_output_ready = IBusSimplePlugin_iBusRsp_stages_0_input_ready;
  assign IBusSimplePlugin_iBusRsp_stages_0_input_payload = IBusSimplePlugin_fetchPc_output_payload;
  assign IBusSimplePlugin_iBusRsp_stages_0_halt = 1'b0;
  assign _zz_51_ = (! IBusSimplePlugin_iBusRsp_stages_0_halt);
  assign IBusSimplePlugin_iBusRsp_stages_0_input_ready = (IBusSimplePlugin_iBusRsp_stages_0_output_ready && _zz_51_);
  assign IBusSimplePlugin_iBusRsp_stages_0_output_valid = (IBusSimplePlugin_iBusRsp_stages_0_input_valid && _zz_51_);
  assign IBusSimplePlugin_iBusRsp_stages_0_output_payload = IBusSimplePlugin_iBusRsp_stages_0_input_payload;
  always @ (*) begin
    IBusSimplePlugin_iBusRsp_stages_1_halt = 1'b0;
    if(((IBusSimplePlugin_cmdFork_pendingFull && (! IBusSimplePlugin_cmdFork_cmdFired)) && (! IBusSimplePlugin_cmdFork_cmdKeep)))begin
      IBusSimplePlugin_iBusRsp_stages_1_halt = 1'b1;
    end
    if(((! IBusSimplePlugin_cmd_ready) && (! IBusSimplePlugin_cmdFork_cmdFired)))begin
      IBusSimplePlugin_iBusRsp_stages_1_halt = 1'b1;
    end
  end

  assign _zz_52_ = (! IBusSimplePlugin_iBusRsp_stages_1_halt);
  assign IBusSimplePlugin_iBusRsp_stages_1_input_ready = (IBusSimplePlugin_iBusRsp_stages_1_output_ready && _zz_52_);
  assign IBusSimplePlugin_iBusRsp_stages_1_output_valid = (IBusSimplePlugin_iBusRsp_stages_1_input_valid && _zz_52_);
  assign IBusSimplePlugin_iBusRsp_stages_1_output_payload = IBusSimplePlugin_iBusRsp_stages_1_input_payload;
  assign IBusSimplePlugin_iBusRsp_stages_2_halt = 1'b0;
  assign _zz_53_ = (! IBusSimplePlugin_iBusRsp_stages_2_halt);
  assign IBusSimplePlugin_iBusRsp_stages_2_input_ready = (IBusSimplePlugin_iBusRsp_stages_2_output_ready && _zz_53_);
  assign IBusSimplePlugin_iBusRsp_stages_2_output_valid = (IBusSimplePlugin_iBusRsp_stages_2_input_valid && _zz_53_);
  assign IBusSimplePlugin_iBusRsp_stages_2_output_payload = IBusSimplePlugin_iBusRsp_stages_2_input_payload;
  assign IBusSimplePlugin_iBusRsp_flush = (IBusSimplePlugin_externalFlush || IBusSimplePlugin_iBusRsp_redoFetch);
  assign IBusSimplePlugin_iBusRsp_stages_0_output_ready = ((1'b0 && (! _zz_54_)) || IBusSimplePlugin_iBusRsp_stages_1_input_ready);
  assign _zz_54_ = _zz_55_;
  assign IBusSimplePlugin_iBusRsp_stages_1_input_valid = _zz_54_;
  assign IBusSimplePlugin_iBusRsp_stages_1_input_payload = _zz_56_;
  assign IBusSimplePlugin_iBusRsp_stages_1_output_ready = ((1'b0 && (! _zz_57_)) || IBusSimplePlugin_iBusRsp_stages_2_input_ready);
  assign _zz_57_ = _zz_58_;
  assign IBusSimplePlugin_iBusRsp_stages_2_input_valid = _zz_57_;
  assign IBusSimplePlugin_iBusRsp_stages_2_input_payload = _zz_59_;
  always @ (*) begin
    IBusSimplePlugin_iBusRsp_readyForError = 1'b1;
    if(IBusSimplePlugin_injector_decodeInput_valid)begin
      IBusSimplePlugin_iBusRsp_readyForError = 1'b0;
    end
    if((! IBusSimplePlugin_pcValids_0))begin
      IBusSimplePlugin_iBusRsp_readyForError = 1'b0;
    end
  end

  assign IBusSimplePlugin_iBusRsp_output_ready = ((1'b0 && (! IBusSimplePlugin_injector_decodeInput_valid)) || IBusSimplePlugin_injector_decodeInput_ready);
  assign IBusSimplePlugin_injector_decodeInput_valid = _zz_60_;
  assign IBusSimplePlugin_injector_decodeInput_payload_pc = _zz_61_;
  assign IBusSimplePlugin_injector_decodeInput_payload_rsp_error = _zz_62_;
  assign IBusSimplePlugin_injector_decodeInput_payload_rsp_inst = _zz_63_;
  assign IBusSimplePlugin_injector_decodeInput_payload_isRvc = _zz_64_;
  assign IBusSimplePlugin_pcValids_0 = IBusSimplePlugin_injector_nextPcCalc_valids_2;
  assign IBusSimplePlugin_pcValids_1 = IBusSimplePlugin_injector_nextPcCalc_valids_3;
  assign IBusSimplePlugin_pcValids_2 = IBusSimplePlugin_injector_nextPcCalc_valids_4;
  assign IBusSimplePlugin_pcValids_3 = IBusSimplePlugin_injector_nextPcCalc_valids_5;
  assign IBusSimplePlugin_injector_decodeInput_ready = (! decode_arbitration_isStuck);
  always @ (*) begin
    decode_arbitration_isValid = IBusSimplePlugin_injector_decodeInput_valid;
    case(_zz_129_)
      3'b000 : begin
      end
      3'b001 : begin
      end
      3'b010 : begin
        decode_arbitration_isValid = 1'b1;
      end
      3'b011 : begin
        decode_arbitration_isValid = 1'b1;
      end
      3'b100 : begin
      end
      default : begin
      end
    endcase
  end

  assign iBus_cmd_valid = IBusSimplePlugin_cmd_valid;
  assign IBusSimplePlugin_cmd_ready = iBus_cmd_ready;
  assign iBus_cmd_payload_pc = IBusSimplePlugin_cmd_payload_pc;
  assign IBusSimplePlugin_pending_next = (_zz_214_ - _zz_218_);
  assign IBusSimplePlugin_cmdFork_pendingFull = (IBusSimplePlugin_pending_value == (3'b111));
  assign IBusSimplePlugin_cmdFork_enterTheMarket = (((IBusSimplePlugin_iBusRsp_stages_1_input_valid && (! IBusSimplePlugin_cmdFork_pendingFull)) && (! IBusSimplePlugin_cmdFork_cmdFired)) && (! IBusSimplePlugin_cmdFork_cmdKeep));
  assign IBusSimplePlugin_cmd_valid = (IBusSimplePlugin_cmdFork_enterTheMarket || IBusSimplePlugin_cmdFork_cmdKeep);
  assign IBusSimplePlugin_pending_inc = IBusSimplePlugin_cmdFork_enterTheMarket;
  assign IBusSimplePlugin_cmd_payload_pc = {IBusSimplePlugin_iBusRsp_stages_1_input_payload[31 : 2],(2'b00)};
  assign IBusSimplePlugin_rspJoin_rspBuffer_flush = ((IBusSimplePlugin_rspJoin_rspBuffer_discardCounter != (3'b000)) || IBusSimplePlugin_iBusRsp_flush);
  assign IBusSimplePlugin_rspJoin_rspBuffer_output_valid = (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid && (IBusSimplePlugin_rspJoin_rspBuffer_discardCounter == (3'b000)));
  assign IBusSimplePlugin_rspJoin_rspBuffer_output_payload_error = IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_error;
  assign IBusSimplePlugin_rspJoin_rspBuffer_output_payload_inst = IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_inst;
  assign _zz_145_ = (IBusSimplePlugin_rspJoin_rspBuffer_output_ready || IBusSimplePlugin_rspJoin_rspBuffer_flush);
  assign IBusSimplePlugin_pending_dec = (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid && _zz_145_);
  assign IBusSimplePlugin_rspJoin_fetchRsp_pc = IBusSimplePlugin_iBusRsp_stages_2_output_payload;
  always @ (*) begin
    IBusSimplePlugin_rspJoin_fetchRsp_rsp_error = IBusSimplePlugin_rspJoin_rspBuffer_output_payload_error;
    if((! IBusSimplePlugin_rspJoin_rspBuffer_output_valid))begin
      IBusSimplePlugin_rspJoin_fetchRsp_rsp_error = 1'b0;
    end
  end

  assign IBusSimplePlugin_rspJoin_fetchRsp_rsp_inst = IBusSimplePlugin_rspJoin_rspBuffer_output_payload_inst;
  assign IBusSimplePlugin_rspJoin_exceptionDetected = 1'b0;
  assign IBusSimplePlugin_rspJoin_join_valid = (IBusSimplePlugin_iBusRsp_stages_2_output_valid && IBusSimplePlugin_rspJoin_rspBuffer_output_valid);
  assign IBusSimplePlugin_rspJoin_join_payload_pc = IBusSimplePlugin_rspJoin_fetchRsp_pc;
  assign IBusSimplePlugin_rspJoin_join_payload_rsp_error = IBusSimplePlugin_rspJoin_fetchRsp_rsp_error;
  assign IBusSimplePlugin_rspJoin_join_payload_rsp_inst = IBusSimplePlugin_rspJoin_fetchRsp_rsp_inst;
  assign IBusSimplePlugin_rspJoin_join_payload_isRvc = IBusSimplePlugin_rspJoin_fetchRsp_isRvc;
  assign IBusSimplePlugin_iBusRsp_stages_2_output_ready = (IBusSimplePlugin_iBusRsp_stages_2_output_valid ? (IBusSimplePlugin_rspJoin_join_valid && IBusSimplePlugin_rspJoin_join_ready) : IBusSimplePlugin_rspJoin_join_ready);
  assign IBusSimplePlugin_rspJoin_rspBuffer_output_ready = (IBusSimplePlugin_rspJoin_join_valid && IBusSimplePlugin_rspJoin_join_ready);
  assign _zz_65_ = (! IBusSimplePlugin_rspJoin_exceptionDetected);
  assign IBusSimplePlugin_rspJoin_join_ready = (IBusSimplePlugin_iBusRsp_output_ready && _zz_65_);
  assign IBusSimplePlugin_iBusRsp_output_valid = (IBusSimplePlugin_rspJoin_join_valid && _zz_65_);
  assign IBusSimplePlugin_iBusRsp_output_payload_pc = IBusSimplePlugin_rspJoin_join_payload_pc;
  assign IBusSimplePlugin_iBusRsp_output_payload_rsp_error = IBusSimplePlugin_rspJoin_join_payload_rsp_error;
  assign IBusSimplePlugin_iBusRsp_output_payload_rsp_inst = IBusSimplePlugin_rspJoin_join_payload_rsp_inst;
  assign IBusSimplePlugin_iBusRsp_output_payload_isRvc = IBusSimplePlugin_rspJoin_join_payload_isRvc;
  assign _zz_66_ = 1'b0;
  always @ (*) begin
    execute_DBusSimplePlugin_skipCmd = 1'b0;
    if(execute_ALIGNEMENT_FAULT)begin
      execute_DBusSimplePlugin_skipCmd = 1'b1;
    end
  end

  assign dBus_cmd_valid = (((((execute_arbitration_isValid && execute_MEMORY_ENABLE) && (! execute_arbitration_isStuckByOthers)) && (! execute_arbitration_isFlushed)) && (! execute_DBusSimplePlugin_skipCmd)) && (! _zz_66_));
  assign dBus_cmd_payload_wr = execute_MEMORY_STORE;
  assign dBus_cmd_payload_size = execute_INSTRUCTION[13 : 12];
  always @ (*) begin
    case(dBus_cmd_payload_size)
      2'b00 : begin
        _zz_67_ = {{{execute_RS2[7 : 0],execute_RS2[7 : 0]},execute_RS2[7 : 0]},execute_RS2[7 : 0]};
      end
      2'b01 : begin
        _zz_67_ = {execute_RS2[15 : 0],execute_RS2[15 : 0]};
      end
      default : begin
        _zz_67_ = execute_RS2[31 : 0];
      end
    endcase
  end

  assign dBus_cmd_payload_data = _zz_67_;
  always @ (*) begin
    case(dBus_cmd_payload_size)
      2'b00 : begin
        _zz_68_ = (4'b0001);
      end
      2'b01 : begin
        _zz_68_ = (4'b0011);
      end
      default : begin
        _zz_68_ = (4'b1111);
      end
    endcase
  end

  assign execute_DBusSimplePlugin_formalMask = (_zz_68_ <<< dBus_cmd_payload_address[1 : 0]);
  assign dBus_cmd_payload_address = execute_SRC_ADD;
  always @ (*) begin
    writeBack_DBusSimplePlugin_rspShifted = writeBack_MEMORY_READ_DATA;
    case(writeBack_MEMORY_ADDRESS_LOW)
      2'b01 : begin
        writeBack_DBusSimplePlugin_rspShifted[7 : 0] = writeBack_MEMORY_READ_DATA[15 : 8];
      end
      2'b10 : begin
        writeBack_DBusSimplePlugin_rspShifted[15 : 0] = writeBack_MEMORY_READ_DATA[31 : 16];
      end
      2'b11 : begin
        writeBack_DBusSimplePlugin_rspShifted[7 : 0] = writeBack_MEMORY_READ_DATA[31 : 24];
      end
      default : begin
      end
    endcase
  end

  assign _zz_69_ = (writeBack_DBusSimplePlugin_rspShifted[7] && (! writeBack_INSTRUCTION[14]));
  always @ (*) begin
    _zz_70_[31] = _zz_69_;
    _zz_70_[30] = _zz_69_;
    _zz_70_[29] = _zz_69_;
    _zz_70_[28] = _zz_69_;
    _zz_70_[27] = _zz_69_;
    _zz_70_[26] = _zz_69_;
    _zz_70_[25] = _zz_69_;
    _zz_70_[24] = _zz_69_;
    _zz_70_[23] = _zz_69_;
    _zz_70_[22] = _zz_69_;
    _zz_70_[21] = _zz_69_;
    _zz_70_[20] = _zz_69_;
    _zz_70_[19] = _zz_69_;
    _zz_70_[18] = _zz_69_;
    _zz_70_[17] = _zz_69_;
    _zz_70_[16] = _zz_69_;
    _zz_70_[15] = _zz_69_;
    _zz_70_[14] = _zz_69_;
    _zz_70_[13] = _zz_69_;
    _zz_70_[12] = _zz_69_;
    _zz_70_[11] = _zz_69_;
    _zz_70_[10] = _zz_69_;
    _zz_70_[9] = _zz_69_;
    _zz_70_[8] = _zz_69_;
    _zz_70_[7 : 0] = writeBack_DBusSimplePlugin_rspShifted[7 : 0];
  end

  assign _zz_71_ = (writeBack_DBusSimplePlugin_rspShifted[15] && (! writeBack_INSTRUCTION[14]));
  always @ (*) begin
    _zz_72_[31] = _zz_71_;
    _zz_72_[30] = _zz_71_;
    _zz_72_[29] = _zz_71_;
    _zz_72_[28] = _zz_71_;
    _zz_72_[27] = _zz_71_;
    _zz_72_[26] = _zz_71_;
    _zz_72_[25] = _zz_71_;
    _zz_72_[24] = _zz_71_;
    _zz_72_[23] = _zz_71_;
    _zz_72_[22] = _zz_71_;
    _zz_72_[21] = _zz_71_;
    _zz_72_[20] = _zz_71_;
    _zz_72_[19] = _zz_71_;
    _zz_72_[18] = _zz_71_;
    _zz_72_[17] = _zz_71_;
    _zz_72_[16] = _zz_71_;
    _zz_72_[15 : 0] = writeBack_DBusSimplePlugin_rspShifted[15 : 0];
  end

  always @ (*) begin
    case(_zz_179_)
      2'b00 : begin
        writeBack_DBusSimplePlugin_rspFormated = _zz_70_;
      end
      2'b01 : begin
        writeBack_DBusSimplePlugin_rspFormated = _zz_72_;
      end
      default : begin
        writeBack_DBusSimplePlugin_rspFormated = writeBack_DBusSimplePlugin_rspShifted;
      end
    endcase
  end

  assign _zz_74_ = ((decode_INSTRUCTION & 32'h00000030) == 32'h00000010);
  assign _zz_75_ = ((decode_INSTRUCTION & 32'h00000004) == 32'h00000004);
  assign _zz_76_ = ((decode_INSTRUCTION & 32'h00001000) == 32'h0);
  assign _zz_77_ = ((decode_INSTRUCTION & 32'h00000048) == 32'h00000048);
  assign _zz_78_ = ((decode_INSTRUCTION & 32'h00004050) == 32'h00004050);
  assign _zz_79_ = ((decode_INSTRUCTION & 32'h00006004) == 32'h00002000);
  assign _zz_73_ = {({(_zz_282_ == _zz_283_),(_zz_284_ == _zz_285_)} != (2'b00)),{({_zz_286_,{_zz_287_,_zz_288_}} != (3'b000)),{({_zz_289_,_zz_290_} != 6'h0),{(_zz_291_ != _zz_292_),{_zz_293_,{_zz_294_,_zz_295_}}}}}};
  assign _zz_80_ = _zz_73_[8 : 7];
  assign _zz_47_ = _zz_80_;
  assign _zz_81_ = _zz_73_[11 : 10];
  assign _zz_46_ = _zz_81_;
  assign _zz_82_ = _zz_73_[15 : 14];
  assign _zz_45_ = _zz_82_;
  assign _zz_83_ = _zz_73_[18 : 17];
  assign _zz_44_ = _zz_83_;
  assign _zz_84_ = _zz_73_[21 : 20];
  assign _zz_43_ = _zz_84_;
  assign _zz_85_ = _zz_73_[26 : 25];
  assign _zz_42_ = _zz_85_;
  assign _zz_86_ = _zz_73_[30 : 29];
  assign _zz_41_ = _zz_86_;
  assign decodeExceptionPort_valid = (decode_arbitration_isValid && (! decode_LEGAL_INSTRUCTION));
  assign decodeExceptionPort_payload_code = (4'b0010);
  assign decodeExceptionPort_payload_badAddr = decode_INSTRUCTION;
  assign decode_RegFilePlugin_regFileReadAddress1 = decode_INSTRUCTION[19 : 15];
  assign decode_RegFilePlugin_regFileReadAddress2 = decode_INSTRUCTION[24 : 20];
  assign decode_RegFilePlugin_rs1Data = _zz_147_;
  assign decode_RegFilePlugin_rs2Data = _zz_148_;
  always @ (*) begin
    lastStageRegFileWrite_valid = (_zz_39_ && writeBack_arbitration_isFiring);
    if(_zz_87_)begin
      lastStageRegFileWrite_valid = 1'b1;
    end
  end

  assign lastStageRegFileWrite_payload_address = _zz_38_[11 : 7];
  assign lastStageRegFileWrite_payload_data = _zz_48_;
  always @ (*) begin
    case(execute_ALU_BITWISE_CTRL)
      `AluBitwiseCtrlEnum_defaultEncoding_AND_1 : begin
        execute_IntAluPlugin_bitwise = (execute_SRC1 & execute_SRC2);
      end
      `AluBitwiseCtrlEnum_defaultEncoding_OR_1 : begin
        execute_IntAluPlugin_bitwise = (execute_SRC1 | execute_SRC2);
      end
      default : begin
        execute_IntAluPlugin_bitwise = (execute_SRC1 ^ execute_SRC2);
      end
    endcase
  end

  always @ (*) begin
    case(execute_ALU_CTRL)
      `AluCtrlEnum_defaultEncoding_BITWISE : begin
        _zz_88_ = execute_IntAluPlugin_bitwise;
      end
      `AluCtrlEnum_defaultEncoding_SLT_SLTU : begin
        _zz_88_ = {31'd0, _zz_221_};
      end
      default : begin
        _zz_88_ = execute_SRC_ADD_SUB;
      end
    endcase
  end

  always @ (*) begin
    case(execute_SRC1_CTRL)
      `Src1CtrlEnum_defaultEncoding_RS : begin
        _zz_89_ = execute_RS1;
      end
      `Src1CtrlEnum_defaultEncoding_PC_INCREMENT : begin
        _zz_89_ = {29'd0, _zz_222_};
      end
      `Src1CtrlEnum_defaultEncoding_IMU : begin
        _zz_89_ = {execute_INSTRUCTION[31 : 12],12'h0};
      end
      default : begin
        _zz_89_ = {27'd0, _zz_223_};
      end
    endcase
  end

  assign _zz_90_ = _zz_224_[11];
  always @ (*) begin
    _zz_91_[19] = _zz_90_;
    _zz_91_[18] = _zz_90_;
    _zz_91_[17] = _zz_90_;
    _zz_91_[16] = _zz_90_;
    _zz_91_[15] = _zz_90_;
    _zz_91_[14] = _zz_90_;
    _zz_91_[13] = _zz_90_;
    _zz_91_[12] = _zz_90_;
    _zz_91_[11] = _zz_90_;
    _zz_91_[10] = _zz_90_;
    _zz_91_[9] = _zz_90_;
    _zz_91_[8] = _zz_90_;
    _zz_91_[7] = _zz_90_;
    _zz_91_[6] = _zz_90_;
    _zz_91_[5] = _zz_90_;
    _zz_91_[4] = _zz_90_;
    _zz_91_[3] = _zz_90_;
    _zz_91_[2] = _zz_90_;
    _zz_91_[1] = _zz_90_;
    _zz_91_[0] = _zz_90_;
  end

  assign _zz_92_ = _zz_225_[11];
  always @ (*) begin
    _zz_93_[19] = _zz_92_;
    _zz_93_[18] = _zz_92_;
    _zz_93_[17] = _zz_92_;
    _zz_93_[16] = _zz_92_;
    _zz_93_[15] = _zz_92_;
    _zz_93_[14] = _zz_92_;
    _zz_93_[13] = _zz_92_;
    _zz_93_[12] = _zz_92_;
    _zz_93_[11] = _zz_92_;
    _zz_93_[10] = _zz_92_;
    _zz_93_[9] = _zz_92_;
    _zz_93_[8] = _zz_92_;
    _zz_93_[7] = _zz_92_;
    _zz_93_[6] = _zz_92_;
    _zz_93_[5] = _zz_92_;
    _zz_93_[4] = _zz_92_;
    _zz_93_[3] = _zz_92_;
    _zz_93_[2] = _zz_92_;
    _zz_93_[1] = _zz_92_;
    _zz_93_[0] = _zz_92_;
  end

  always @ (*) begin
    case(execute_SRC2_CTRL)
      `Src2CtrlEnum_defaultEncoding_RS : begin
        _zz_94_ = execute_RS2;
      end
      `Src2CtrlEnum_defaultEncoding_IMI : begin
        _zz_94_ = {_zz_91_,execute_INSTRUCTION[31 : 20]};
      end
      `Src2CtrlEnum_defaultEncoding_IMS : begin
        _zz_94_ = {_zz_93_,{execute_INSTRUCTION[31 : 25],execute_INSTRUCTION[11 : 7]}};
      end
      default : begin
        _zz_94_ = _zz_33_;
      end
    endcase
  end

  always @ (*) begin
    execute_SrcPlugin_addSub = _zz_226_;
    if(execute_SRC2_FORCE_ZERO)begin
      execute_SrcPlugin_addSub = execute_SRC1;
    end
  end

  assign execute_SrcPlugin_less = ((execute_SRC1[31] == execute_SRC2[31]) ? execute_SrcPlugin_addSub[31] : (execute_SRC_LESS_UNSIGNED ? execute_SRC2[31] : execute_SRC1[31]));
  assign execute_FullBarrelShifterPlugin_amplitude = execute_SRC2[4 : 0];
  always @ (*) begin
    _zz_95_[0] = execute_SRC1[31];
    _zz_95_[1] = execute_SRC1[30];
    _zz_95_[2] = execute_SRC1[29];
    _zz_95_[3] = execute_SRC1[28];
    _zz_95_[4] = execute_SRC1[27];
    _zz_95_[5] = execute_SRC1[26];
    _zz_95_[6] = execute_SRC1[25];
    _zz_95_[7] = execute_SRC1[24];
    _zz_95_[8] = execute_SRC1[23];
    _zz_95_[9] = execute_SRC1[22];
    _zz_95_[10] = execute_SRC1[21];
    _zz_95_[11] = execute_SRC1[20];
    _zz_95_[12] = execute_SRC1[19];
    _zz_95_[13] = execute_SRC1[18];
    _zz_95_[14] = execute_SRC1[17];
    _zz_95_[15] = execute_SRC1[16];
    _zz_95_[16] = execute_SRC1[15];
    _zz_95_[17] = execute_SRC1[14];
    _zz_95_[18] = execute_SRC1[13];
    _zz_95_[19] = execute_SRC1[12];
    _zz_95_[20] = execute_SRC1[11];
    _zz_95_[21] = execute_SRC1[10];
    _zz_95_[22] = execute_SRC1[9];
    _zz_95_[23] = execute_SRC1[8];
    _zz_95_[24] = execute_SRC1[7];
    _zz_95_[25] = execute_SRC1[6];
    _zz_95_[26] = execute_SRC1[5];
    _zz_95_[27] = execute_SRC1[4];
    _zz_95_[28] = execute_SRC1[3];
    _zz_95_[29] = execute_SRC1[2];
    _zz_95_[30] = execute_SRC1[1];
    _zz_95_[31] = execute_SRC1[0];
  end

  assign execute_FullBarrelShifterPlugin_reversed = ((execute_SHIFT_CTRL == `ShiftCtrlEnum_defaultEncoding_SLL_1) ? _zz_95_ : execute_SRC1);
  always @ (*) begin
    _zz_96_[0] = execute_SHIFT_RIGHT[31];
    _zz_96_[1] = execute_SHIFT_RIGHT[30];
    _zz_96_[2] = execute_SHIFT_RIGHT[29];
    _zz_96_[3] = execute_SHIFT_RIGHT[28];
    _zz_96_[4] = execute_SHIFT_RIGHT[27];
    _zz_96_[5] = execute_SHIFT_RIGHT[26];
    _zz_96_[6] = execute_SHIFT_RIGHT[25];
    _zz_96_[7] = execute_SHIFT_RIGHT[24];
    _zz_96_[8] = execute_SHIFT_RIGHT[23];
    _zz_96_[9] = execute_SHIFT_RIGHT[22];
    _zz_96_[10] = execute_SHIFT_RIGHT[21];
    _zz_96_[11] = execute_SHIFT_RIGHT[20];
    _zz_96_[12] = execute_SHIFT_RIGHT[19];
    _zz_96_[13] = execute_SHIFT_RIGHT[18];
    _zz_96_[14] = execute_SHIFT_RIGHT[17];
    _zz_96_[15] = execute_SHIFT_RIGHT[16];
    _zz_96_[16] = execute_SHIFT_RIGHT[15];
    _zz_96_[17] = execute_SHIFT_RIGHT[14];
    _zz_96_[18] = execute_SHIFT_RIGHT[13];
    _zz_96_[19] = execute_SHIFT_RIGHT[12];
    _zz_96_[20] = execute_SHIFT_RIGHT[11];
    _zz_96_[21] = execute_SHIFT_RIGHT[10];
    _zz_96_[22] = execute_SHIFT_RIGHT[9];
    _zz_96_[23] = execute_SHIFT_RIGHT[8];
    _zz_96_[24] = execute_SHIFT_RIGHT[7];
    _zz_96_[25] = execute_SHIFT_RIGHT[6];
    _zz_96_[26] = execute_SHIFT_RIGHT[5];
    _zz_96_[27] = execute_SHIFT_RIGHT[4];
    _zz_96_[28] = execute_SHIFT_RIGHT[3];
    _zz_96_[29] = execute_SHIFT_RIGHT[2];
    _zz_96_[30] = execute_SHIFT_RIGHT[1];
    _zz_96_[31] = execute_SHIFT_RIGHT[0];
  end

  always @ (*) begin
    _zz_97_ = 1'b0;
    if(_zz_163_)begin
      if(_zz_164_)begin
        if(_zz_102_)begin
          _zz_97_ = 1'b1;
        end
      end
    end
    if(_zz_165_)begin
      if(_zz_166_)begin
        if(_zz_104_)begin
          _zz_97_ = 1'b1;
        end
      end
    end
    if(_zz_167_)begin
      if(_zz_168_)begin
        if(_zz_106_)begin
          _zz_97_ = 1'b1;
        end
      end
    end
    if((! decode_RS1_USE))begin
      _zz_97_ = 1'b0;
    end
  end

  always @ (*) begin
    _zz_98_ = 1'b0;
    if(_zz_163_)begin
      if(_zz_164_)begin
        if(_zz_103_)begin
          _zz_98_ = 1'b1;
        end
      end
    end
    if(_zz_165_)begin
      if(_zz_166_)begin
        if(_zz_105_)begin
          _zz_98_ = 1'b1;
        end
      end
    end
    if(_zz_167_)begin
      if(_zz_168_)begin
        if(_zz_107_)begin
          _zz_98_ = 1'b1;
        end
      end
    end
    if((! decode_RS2_USE))begin
      _zz_98_ = 1'b0;
    end
  end

  assign _zz_102_ = (writeBack_INSTRUCTION[11 : 7] == decode_INSTRUCTION[19 : 15]);
  assign _zz_103_ = (writeBack_INSTRUCTION[11 : 7] == decode_INSTRUCTION[24 : 20]);
  assign _zz_104_ = (memory_INSTRUCTION[11 : 7] == decode_INSTRUCTION[19 : 15]);
  assign _zz_105_ = (memory_INSTRUCTION[11 : 7] == decode_INSTRUCTION[24 : 20]);
  assign _zz_106_ = (execute_INSTRUCTION[11 : 7] == decode_INSTRUCTION[19 : 15]);
  assign _zz_107_ = (execute_INSTRUCTION[11 : 7] == decode_INSTRUCTION[24 : 20]);
  assign execute_MulPlugin_a = execute_RS1;
  assign execute_MulPlugin_b = execute_RS2;
  always @ (*) begin
    case(_zz_169_)
      2'b01 : begin
        execute_MulPlugin_aSigned = 1'b1;
      end
      2'b10 : begin
        execute_MulPlugin_aSigned = 1'b1;
      end
      default : begin
        execute_MulPlugin_aSigned = 1'b0;
      end
    endcase
  end

  always @ (*) begin
    case(_zz_169_)
      2'b01 : begin
        execute_MulPlugin_bSigned = 1'b1;
      end
      2'b10 : begin
        execute_MulPlugin_bSigned = 1'b0;
      end
      default : begin
        execute_MulPlugin_bSigned = 1'b0;
      end
    endcase
  end

  assign execute_MulPlugin_aULow = execute_MulPlugin_a[15 : 0];
  assign execute_MulPlugin_bULow = execute_MulPlugin_b[15 : 0];
  assign execute_MulPlugin_aSLow = {1'b0,execute_MulPlugin_a[15 : 0]};
  assign execute_MulPlugin_bSLow = {1'b0,execute_MulPlugin_b[15 : 0]};
  assign execute_MulPlugin_aHigh = {(execute_MulPlugin_aSigned && execute_MulPlugin_a[31]),execute_MulPlugin_a[31 : 16]};
  assign execute_MulPlugin_bHigh = {(execute_MulPlugin_bSigned && execute_MulPlugin_b[31]),execute_MulPlugin_b[31 : 16]};
  assign writeBack_MulPlugin_result = ($signed(_zz_233_) + $signed(_zz_234_));
  assign memory_DivPlugin_frontendOk = 1'b1;
  always @ (*) begin
    memory_DivPlugin_div_counter_willIncrement = 1'b0;
    if(_zz_149_)begin
      if(_zz_170_)begin
        memory_DivPlugin_div_counter_willIncrement = 1'b1;
      end
    end
  end

  always @ (*) begin
    memory_DivPlugin_div_counter_willClear = 1'b0;
    if(_zz_171_)begin
      memory_DivPlugin_div_counter_willClear = 1'b1;
    end
  end

  assign memory_DivPlugin_div_counter_willOverflowIfInc = (memory_DivPlugin_div_counter_value == 6'h21);
  assign memory_DivPlugin_div_counter_willOverflow = (memory_DivPlugin_div_counter_willOverflowIfInc && memory_DivPlugin_div_counter_willIncrement);
  always @ (*) begin
    if(memory_DivPlugin_div_counter_willOverflow)begin
      memory_DivPlugin_div_counter_valueNext = 6'h0;
    end else begin
      memory_DivPlugin_div_counter_valueNext = (memory_DivPlugin_div_counter_value + _zz_238_);
    end
    if(memory_DivPlugin_div_counter_willClear)begin
      memory_DivPlugin_div_counter_valueNext = 6'h0;
    end
  end

  assign _zz_108_ = memory_DivPlugin_rs1[31 : 0];
  assign memory_DivPlugin_div_stage_0_remainderShifted = {memory_DivPlugin_accumulator[31 : 0],_zz_108_[31]};
  assign memory_DivPlugin_div_stage_0_remainderMinusDenominator = (memory_DivPlugin_div_stage_0_remainderShifted - _zz_239_);
  assign memory_DivPlugin_div_stage_0_outRemainder = ((! memory_DivPlugin_div_stage_0_remainderMinusDenominator[32]) ? _zz_240_ : _zz_241_);
  assign memory_DivPlugin_div_stage_0_outNumerator = _zz_242_[31:0];
  assign _zz_109_ = (memory_INSTRUCTION[13] ? memory_DivPlugin_accumulator[31 : 0] : memory_DivPlugin_rs1[31 : 0]);
  assign _zz_110_ = (execute_RS2[31] && execute_IS_RS2_SIGNED);
  assign _zz_111_ = (1'b0 || ((execute_IS_DIV && execute_RS1[31]) && execute_IS_RS1_SIGNED));
  always @ (*) begin
    _zz_112_[32] = (execute_IS_RS1_SIGNED && execute_RS1[31]);
    _zz_112_[31 : 0] = execute_RS1;
  end

  always @ (*) begin
    CsrPlugin_privilege = (2'b11);
    if(CsrPlugin_forceMachineWire)begin
      CsrPlugin_privilege = (2'b11);
    end
  end

  assign _zz_113_ = (CsrPlugin_mip_MTIP && CsrPlugin_mie_MTIE);
  assign _zz_114_ = (CsrPlugin_mip_MSIP && CsrPlugin_mie_MSIE);
  assign _zz_115_ = (CsrPlugin_mip_MEIP && CsrPlugin_mie_MEIE);
  assign CsrPlugin_exceptionPortCtrl_exceptionTargetPrivilegeUncapped = (2'b11);
  assign CsrPlugin_exceptionPortCtrl_exceptionTargetPrivilege = ((CsrPlugin_privilege < CsrPlugin_exceptionPortCtrl_exceptionTargetPrivilegeUncapped) ? CsrPlugin_exceptionPortCtrl_exceptionTargetPrivilegeUncapped : CsrPlugin_privilege);
  assign _zz_116_ = {BranchPlugin_branchExceptionPort_valid,CsrPlugin_selfException_valid};
  assign _zz_117_ = _zz_252_[0];
  always @ (*) begin
    CsrPlugin_exceptionPortCtrl_exceptionValids_decode = CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_decode;
    if(decodeExceptionPort_valid)begin
      CsrPlugin_exceptionPortCtrl_exceptionValids_decode = 1'b1;
    end
    if(decode_arbitration_isFlushed)begin
      CsrPlugin_exceptionPortCtrl_exceptionValids_decode = 1'b0;
    end
  end

  always @ (*) begin
    CsrPlugin_exceptionPortCtrl_exceptionValids_execute = CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_execute;
    if(_zz_157_)begin
      CsrPlugin_exceptionPortCtrl_exceptionValids_execute = 1'b1;
    end
    if(execute_arbitration_isFlushed)begin
      CsrPlugin_exceptionPortCtrl_exceptionValids_execute = 1'b0;
    end
  end

  always @ (*) begin
    CsrPlugin_exceptionPortCtrl_exceptionValids_memory = CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_memory;
    if(memory_arbitration_isFlushed)begin
      CsrPlugin_exceptionPortCtrl_exceptionValids_memory = 1'b0;
    end
  end

  always @ (*) begin
    CsrPlugin_exceptionPortCtrl_exceptionValids_writeBack = CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_writeBack;
    if(writeBack_arbitration_isFlushed)begin
      CsrPlugin_exceptionPortCtrl_exceptionValids_writeBack = 1'b0;
    end
  end

  assign CsrPlugin_exceptionPendings_0 = CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_decode;
  assign CsrPlugin_exceptionPendings_1 = CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_execute;
  assign CsrPlugin_exceptionPendings_2 = CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_memory;
  assign CsrPlugin_exceptionPendings_3 = CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_writeBack;
  assign CsrPlugin_exception = (CsrPlugin_exceptionPortCtrl_exceptionValids_writeBack && CsrPlugin_allowException);
  assign CsrPlugin_pipelineLiberator_active = ((CsrPlugin_interrupt_valid && CsrPlugin_allowInterrupts) && decode_arbitration_isValid);
  always @ (*) begin
    CsrPlugin_pipelineLiberator_done = CsrPlugin_pipelineLiberator_pcValids_2;
    if(({CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_writeBack,{CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_memory,CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_execute}} != (3'b000)))begin
      CsrPlugin_pipelineLiberator_done = 1'b0;
    end
    if(CsrPlugin_hadException)begin
      CsrPlugin_pipelineLiberator_done = 1'b0;
    end
  end

  assign CsrPlugin_interruptJump = ((CsrPlugin_interrupt_valid && CsrPlugin_pipelineLiberator_done) && CsrPlugin_allowInterrupts);
  always @ (*) begin
    CsrPlugin_targetPrivilege = CsrPlugin_interrupt_targetPrivilege;
    if(CsrPlugin_hadException)begin
      CsrPlugin_targetPrivilege = CsrPlugin_exceptionPortCtrl_exceptionTargetPrivilege;
    end
  end

  always @ (*) begin
    CsrPlugin_trapCause = CsrPlugin_interrupt_code;
    if(CsrPlugin_hadException)begin
      CsrPlugin_trapCause = CsrPlugin_exceptionPortCtrl_exceptionContext_code;
    end
  end

  always @ (*) begin
    CsrPlugin_xtvec_mode = (2'bxx);
    case(CsrPlugin_targetPrivilege)
      2'b11 : begin
        CsrPlugin_xtvec_mode = CsrPlugin_mtvec_mode;
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    CsrPlugin_xtvec_base = 30'h0;
    case(CsrPlugin_targetPrivilege)
      2'b11 : begin
        CsrPlugin_xtvec_base = CsrPlugin_mtvec_base;
      end
      default : begin
      end
    endcase
  end

  assign contextSwitching = CsrPlugin_jumpInterface_valid;
  assign execute_CsrPlugin_blockedBySideEffects = ({writeBack_arbitration_isValid,memory_arbitration_isValid} != (2'b00));
  always @ (*) begin
    execute_CsrPlugin_illegalAccess = 1'b1;
    if(execute_CsrPlugin_csr_3857)begin
      if(execute_CSR_READ_OPCODE)begin
        execute_CsrPlugin_illegalAccess = 1'b0;
      end
    end
    if(execute_CsrPlugin_csr_3858)begin
      if(execute_CSR_READ_OPCODE)begin
        execute_CsrPlugin_illegalAccess = 1'b0;
      end
    end
    if(execute_CsrPlugin_csr_3859)begin
      if(execute_CSR_READ_OPCODE)begin
        execute_CsrPlugin_illegalAccess = 1'b0;
      end
    end
    if(execute_CsrPlugin_csr_3860)begin
      if(execute_CSR_READ_OPCODE)begin
        execute_CsrPlugin_illegalAccess = 1'b0;
      end
    end
    if(execute_CsrPlugin_csr_769)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_768)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_836)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_772)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_773)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_833)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_832)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_834)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_835)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_2816)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_2944)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_2818)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_2946)begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
    if(execute_CsrPlugin_csr_3072)begin
      if(execute_CSR_READ_OPCODE)begin
        execute_CsrPlugin_illegalAccess = 1'b0;
      end
    end
    if(execute_CsrPlugin_csr_3200)begin
      if(execute_CSR_READ_OPCODE)begin
        execute_CsrPlugin_illegalAccess = 1'b0;
      end
    end
    if((CsrPlugin_privilege < execute_CsrPlugin_csrAddress[9 : 8]))begin
      execute_CsrPlugin_illegalAccess = 1'b1;
    end
    if(((! execute_arbitration_isValid) || (! execute_IS_CSR)))begin
      execute_CsrPlugin_illegalAccess = 1'b0;
    end
  end

  always @ (*) begin
    execute_CsrPlugin_illegalInstruction = 1'b0;
    if((execute_arbitration_isValid && (execute_ENV_CTRL == `EnvCtrlEnum_defaultEncoding_XRET)))begin
      if((CsrPlugin_privilege < execute_INSTRUCTION[29 : 28]))begin
        execute_CsrPlugin_illegalInstruction = 1'b1;
      end
    end
  end

  always @ (*) begin
    CsrPlugin_selfException_valid = 1'b0;
    if(_zz_172_)begin
      CsrPlugin_selfException_valid = 1'b1;
    end
    if(_zz_173_)begin
      CsrPlugin_selfException_valid = 1'b1;
    end
  end

  always @ (*) begin
    CsrPlugin_selfException_payload_code = (4'bxxxx);
    if(_zz_172_)begin
      CsrPlugin_selfException_payload_code = (4'b0010);
    end
    if(_zz_173_)begin
      case(CsrPlugin_privilege)
        2'b00 : begin
          CsrPlugin_selfException_payload_code = (4'b1000);
        end
        default : begin
          CsrPlugin_selfException_payload_code = (4'b1011);
        end
      endcase
    end
  end

  assign CsrPlugin_selfException_payload_badAddr = execute_INSTRUCTION;
  assign execute_CsrPlugin_writeInstruction = ((execute_arbitration_isValid && execute_IS_CSR) && execute_CSR_WRITE_OPCODE);
  assign execute_CsrPlugin_readInstruction = ((execute_arbitration_isValid && execute_IS_CSR) && execute_CSR_READ_OPCODE);
  assign execute_CsrPlugin_writeEnable = ((execute_CsrPlugin_writeInstruction && (! execute_CsrPlugin_blockedBySideEffects)) && (! execute_arbitration_isStuckByOthers));
  assign execute_CsrPlugin_readEnable = ((execute_CsrPlugin_readInstruction && (! execute_CsrPlugin_blockedBySideEffects)) && (! execute_arbitration_isStuckByOthers));
  assign execute_CsrPlugin_readToWriteData = execute_CsrPlugin_readData;
  always @ (*) begin
    case(_zz_181_)
      1'b0 : begin
        execute_CsrPlugin_writeData = execute_SRC1;
      end
      default : begin
        execute_CsrPlugin_writeData = (execute_INSTRUCTION[12] ? (execute_CsrPlugin_readToWriteData & (~ execute_SRC1)) : (execute_CsrPlugin_readToWriteData | execute_SRC1));
      end
    endcase
  end

  assign execute_CsrPlugin_csrAddress = execute_INSTRUCTION[31 : 20];
  assign execute_BranchPlugin_eq = (execute_SRC1 == execute_SRC2);
  assign _zz_118_ = execute_INSTRUCTION[14 : 12];
  always @ (*) begin
    if((_zz_118_ == (3'b000))) begin
        _zz_119_ = execute_BranchPlugin_eq;
    end else if((_zz_118_ == (3'b001))) begin
        _zz_119_ = (! execute_BranchPlugin_eq);
    end else if((((_zz_118_ & (3'b101)) == (3'b101)))) begin
        _zz_119_ = (! execute_SRC_LESS);
    end else begin
        _zz_119_ = execute_SRC_LESS;
    end
  end

  always @ (*) begin
    case(execute_BRANCH_CTRL)
      `BranchCtrlEnum_defaultEncoding_INC : begin
        _zz_120_ = 1'b0;
      end
      `BranchCtrlEnum_defaultEncoding_JAL : begin
        _zz_120_ = 1'b1;
      end
      `BranchCtrlEnum_defaultEncoding_JALR : begin
        _zz_120_ = 1'b1;
      end
      default : begin
        _zz_120_ = _zz_119_;
      end
    endcase
  end

  assign execute_BranchPlugin_branch_src1 = ((execute_BRANCH_CTRL == `BranchCtrlEnum_defaultEncoding_JALR) ? execute_RS1 : execute_PC);
  assign _zz_121_ = _zz_254_[19];
  always @ (*) begin
    _zz_122_[10] = _zz_121_;
    _zz_122_[9] = _zz_121_;
    _zz_122_[8] = _zz_121_;
    _zz_122_[7] = _zz_121_;
    _zz_122_[6] = _zz_121_;
    _zz_122_[5] = _zz_121_;
    _zz_122_[4] = _zz_121_;
    _zz_122_[3] = _zz_121_;
    _zz_122_[2] = _zz_121_;
    _zz_122_[1] = _zz_121_;
    _zz_122_[0] = _zz_121_;
  end

  assign _zz_123_ = _zz_255_[11];
  always @ (*) begin
    _zz_124_[19] = _zz_123_;
    _zz_124_[18] = _zz_123_;
    _zz_124_[17] = _zz_123_;
    _zz_124_[16] = _zz_123_;
    _zz_124_[15] = _zz_123_;
    _zz_124_[14] = _zz_123_;
    _zz_124_[13] = _zz_123_;
    _zz_124_[12] = _zz_123_;
    _zz_124_[11] = _zz_123_;
    _zz_124_[10] = _zz_123_;
    _zz_124_[9] = _zz_123_;
    _zz_124_[8] = _zz_123_;
    _zz_124_[7] = _zz_123_;
    _zz_124_[6] = _zz_123_;
    _zz_124_[5] = _zz_123_;
    _zz_124_[4] = _zz_123_;
    _zz_124_[3] = _zz_123_;
    _zz_124_[2] = _zz_123_;
    _zz_124_[1] = _zz_123_;
    _zz_124_[0] = _zz_123_;
  end

  assign _zz_125_ = _zz_256_[11];
  always @ (*) begin
    _zz_126_[18] = _zz_125_;
    _zz_126_[17] = _zz_125_;
    _zz_126_[16] = _zz_125_;
    _zz_126_[15] = _zz_125_;
    _zz_126_[14] = _zz_125_;
    _zz_126_[13] = _zz_125_;
    _zz_126_[12] = _zz_125_;
    _zz_126_[11] = _zz_125_;
    _zz_126_[10] = _zz_125_;
    _zz_126_[9] = _zz_125_;
    _zz_126_[8] = _zz_125_;
    _zz_126_[7] = _zz_125_;
    _zz_126_[6] = _zz_125_;
    _zz_126_[5] = _zz_125_;
    _zz_126_[4] = _zz_125_;
    _zz_126_[3] = _zz_125_;
    _zz_126_[2] = _zz_125_;
    _zz_126_[1] = _zz_125_;
    _zz_126_[0] = _zz_125_;
  end

  always @ (*) begin
    case(execute_BRANCH_CTRL)
      `BranchCtrlEnum_defaultEncoding_JAL : begin
        _zz_127_ = {{_zz_122_,{{{execute_INSTRUCTION[31],execute_INSTRUCTION[19 : 12]},execute_INSTRUCTION[20]},execute_INSTRUCTION[30 : 21]}},1'b0};
      end
      `BranchCtrlEnum_defaultEncoding_JALR : begin
        _zz_127_ = {_zz_124_,execute_INSTRUCTION[31 : 20]};
      end
      default : begin
        _zz_127_ = {{_zz_126_,{{{execute_INSTRUCTION[31],execute_INSTRUCTION[7]},execute_INSTRUCTION[30 : 25]},execute_INSTRUCTION[11 : 8]}},1'b0};
      end
    endcase
  end

  assign execute_BranchPlugin_branch_src2 = _zz_127_;
  assign execute_BranchPlugin_branchAdder = (execute_BranchPlugin_branch_src1 + execute_BranchPlugin_branch_src2);
  assign BranchPlugin_jumpInterface_valid = ((execute_arbitration_isValid && execute_BRANCH_DO) && (! 1'b0));
  assign BranchPlugin_jumpInterface_payload = execute_BRANCH_CALC;
  always @ (*) begin
    BranchPlugin_branchExceptionPort_valid = ((execute_arbitration_isValid && execute_BRANCH_DO) && BranchPlugin_jumpInterface_payload[1]);
    if(1'b0)begin
      BranchPlugin_branchExceptionPort_valid = 1'b0;
    end
  end

  assign BranchPlugin_branchExceptionPort_payload_code = (4'b0000);
  assign BranchPlugin_branchExceptionPort_payload_badAddr = BranchPlugin_jumpInterface_payload;
  always @ (*) begin
    debug_bus_cmd_ready = 1'b1;
    if(debug_bus_cmd_valid)begin
      case(_zz_174_)
        6'b000000 : begin
        end
        6'b000001 : begin
          if(debug_bus_cmd_payload_wr)begin
            debug_bus_cmd_ready = IBusSimplePlugin_injectionPort_ready;
          end
        end
        default : begin
        end
      endcase
    end
  end

  always @ (*) begin
    debug_bus_rsp_data = DebugPlugin_busReadDataReg;
    if((! _zz_128_))begin
      debug_bus_rsp_data[0] = DebugPlugin_resetIt;
      debug_bus_rsp_data[1] = DebugPlugin_haltIt;
      debug_bus_rsp_data[2] = DebugPlugin_isPipBusy;
      debug_bus_rsp_data[3] = DebugPlugin_haltedByBreak;
      debug_bus_rsp_data[4] = DebugPlugin_stepIt;
    end
  end

  always @ (*) begin
    IBusSimplePlugin_injectionPort_valid = 1'b0;
    if(debug_bus_cmd_valid)begin
      case(_zz_174_)
        6'b000000 : begin
        end
        6'b000001 : begin
          if(debug_bus_cmd_payload_wr)begin
            IBusSimplePlugin_injectionPort_valid = 1'b1;
          end
        end
        default : begin
        end
      endcase
    end
  end

  assign IBusSimplePlugin_injectionPort_payload = debug_bus_cmd_payload_data;
  assign debug_resetOut = DebugPlugin_resetIt_regNext;
  assign _zz_25_ = decode_SHIFT_CTRL;
  assign _zz_23_ = _zz_41_;
  assign _zz_32_ = decode_to_execute_SHIFT_CTRL;
  assign _zz_22_ = decode_BRANCH_CTRL;
  assign _zz_20_ = _zz_47_;
  assign _zz_26_ = decode_to_execute_BRANCH_CTRL;
  assign _zz_19_ = decode_SRC1_CTRL;
  assign _zz_17_ = _zz_46_;
  assign _zz_35_ = decode_to_execute_SRC1_CTRL;
  assign _zz_16_ = decode_ENV_CTRL;
  assign _zz_13_ = execute_ENV_CTRL;
  assign _zz_11_ = memory_ENV_CTRL;
  assign _zz_14_ = _zz_42_;
  assign _zz_28_ = decode_to_execute_ENV_CTRL;
  assign _zz_27_ = execute_to_memory_ENV_CTRL;
  assign _zz_29_ = memory_to_writeBack_ENV_CTRL;
  assign _zz_9_ = decode_ALU_CTRL;
  assign _zz_7_ = _zz_43_;
  assign _zz_36_ = decode_to_execute_ALU_CTRL;
  assign _zz_6_ = decode_SRC2_CTRL;
  assign _zz_4_ = _zz_44_;
  assign _zz_34_ = decode_to_execute_SRC2_CTRL;
  assign _zz_3_ = decode_ALU_BITWISE_CTRL;
  assign _zz_1_ = _zz_45_;
  assign _zz_37_ = decode_to_execute_ALU_BITWISE_CTRL;
  assign decode_arbitration_isFlushed = (({writeBack_arbitration_flushNext,{memory_arbitration_flushNext,execute_arbitration_flushNext}} != (3'b000)) || ({writeBack_arbitration_flushIt,{memory_arbitration_flushIt,{execute_arbitration_flushIt,decode_arbitration_flushIt}}} != (4'b0000)));
  assign execute_arbitration_isFlushed = (({writeBack_arbitration_flushNext,memory_arbitration_flushNext} != (2'b00)) || ({writeBack_arbitration_flushIt,{memory_arbitration_flushIt,execute_arbitration_flushIt}} != (3'b000)));
  assign memory_arbitration_isFlushed = ((writeBack_arbitration_flushNext != (1'b0)) || ({writeBack_arbitration_flushIt,memory_arbitration_flushIt} != (2'b00)));
  assign writeBack_arbitration_isFlushed = (1'b0 || (writeBack_arbitration_flushIt != (1'b0)));
  assign decode_arbitration_isStuckByOthers = (decode_arbitration_haltByOther || (((1'b0 || execute_arbitration_isStuck) || memory_arbitration_isStuck) || writeBack_arbitration_isStuck));
  assign decode_arbitration_isStuck = (decode_arbitration_haltItself || decode_arbitration_isStuckByOthers);
  assign decode_arbitration_isMoving = ((! decode_arbitration_isStuck) && (! decode_arbitration_removeIt));
  assign decode_arbitration_isFiring = ((decode_arbitration_isValid && (! decode_arbitration_isStuck)) && (! decode_arbitration_removeIt));
  assign execute_arbitration_isStuckByOthers = (execute_arbitration_haltByOther || ((1'b0 || memory_arbitration_isStuck) || writeBack_arbitration_isStuck));
  assign execute_arbitration_isStuck = (execute_arbitration_haltItself || execute_arbitration_isStuckByOthers);
  assign execute_arbitration_isMoving = ((! execute_arbitration_isStuck) && (! execute_arbitration_removeIt));
  assign execute_arbitration_isFiring = ((execute_arbitration_isValid && (! execute_arbitration_isStuck)) && (! execute_arbitration_removeIt));
  assign memory_arbitration_isStuckByOthers = (memory_arbitration_haltByOther || (1'b0 || writeBack_arbitration_isStuck));
  assign memory_arbitration_isStuck = (memory_arbitration_haltItself || memory_arbitration_isStuckByOthers);
  assign memory_arbitration_isMoving = ((! memory_arbitration_isStuck) && (! memory_arbitration_removeIt));
  assign memory_arbitration_isFiring = ((memory_arbitration_isValid && (! memory_arbitration_isStuck)) && (! memory_arbitration_removeIt));
  assign writeBack_arbitration_isStuckByOthers = (writeBack_arbitration_haltByOther || 1'b0);
  assign writeBack_arbitration_isStuck = (writeBack_arbitration_haltItself || writeBack_arbitration_isStuckByOthers);
  assign writeBack_arbitration_isMoving = ((! writeBack_arbitration_isStuck) && (! writeBack_arbitration_removeIt));
  assign writeBack_arbitration_isFiring = ((writeBack_arbitration_isValid && (! writeBack_arbitration_isStuck)) && (! writeBack_arbitration_removeIt));
  always @ (*) begin
    IBusSimplePlugin_injectionPort_ready = 1'b0;
    case(_zz_129_)
      3'b000 : begin
      end
      3'b001 : begin
      end
      3'b010 : begin
      end
      3'b011 : begin
      end
      3'b100 : begin
        IBusSimplePlugin_injectionPort_ready = 1'b1;
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_130_ = 32'h0;
    if(execute_CsrPlugin_csr_769)begin
      _zz_130_[31 : 30] = CsrPlugin_misa_base;
      _zz_130_[25 : 0] = CsrPlugin_misa_extensions;
    end
  end

  always @ (*) begin
    _zz_131_ = 32'h0;
    if(execute_CsrPlugin_csr_768)begin
      _zz_131_[12 : 11] = CsrPlugin_mstatus_MPP;
      _zz_131_[7 : 7] = CsrPlugin_mstatus_MPIE;
      _zz_131_[3 : 3] = CsrPlugin_mstatus_MIE;
    end
  end

  always @ (*) begin
    _zz_132_ = 32'h0;
    if(execute_CsrPlugin_csr_836)begin
      _zz_132_[11 : 11] = CsrPlugin_mip_MEIP;
      _zz_132_[7 : 7] = CsrPlugin_mip_MTIP;
      _zz_132_[3 : 3] = CsrPlugin_mip_MSIP;
    end
  end

  always @ (*) begin
    _zz_133_ = 32'h0;
    if(execute_CsrPlugin_csr_772)begin
      _zz_133_[11 : 11] = CsrPlugin_mie_MEIE;
      _zz_133_[7 : 7] = CsrPlugin_mie_MTIE;
      _zz_133_[3 : 3] = CsrPlugin_mie_MSIE;
    end
  end

  always @ (*) begin
    _zz_134_ = 32'h0;
    if(execute_CsrPlugin_csr_773)begin
      _zz_134_[31 : 2] = CsrPlugin_mtvec_base;
      _zz_134_[1 : 0] = CsrPlugin_mtvec_mode;
    end
  end

  always @ (*) begin
    _zz_135_ = 32'h0;
    if(execute_CsrPlugin_csr_833)begin
      _zz_135_[31 : 0] = CsrPlugin_mepc;
    end
  end

  always @ (*) begin
    _zz_136_ = 32'h0;
    if(execute_CsrPlugin_csr_832)begin
      _zz_136_[31 : 0] = CsrPlugin_mscratch;
    end
  end

  always @ (*) begin
    _zz_137_ = 32'h0;
    if(execute_CsrPlugin_csr_834)begin
      _zz_137_[31 : 31] = CsrPlugin_mcause_interrupt;
      _zz_137_[3 : 0] = CsrPlugin_mcause_exceptionCode;
    end
  end

  always @ (*) begin
    _zz_138_ = 32'h0;
    if(execute_CsrPlugin_csr_835)begin
      _zz_138_[31 : 0] = CsrPlugin_mtval;
    end
  end

  always @ (*) begin
    _zz_139_ = 32'h0;
    if(execute_CsrPlugin_csr_2816)begin
      _zz_139_[31 : 0] = CsrPlugin_mcycle[31 : 0];
    end
  end

  always @ (*) begin
    _zz_140_ = 32'h0;
    if(execute_CsrPlugin_csr_2944)begin
      _zz_140_[31 : 0] = CsrPlugin_mcycle[63 : 32];
    end
  end

  always @ (*) begin
    _zz_141_ = 32'h0;
    if(execute_CsrPlugin_csr_2818)begin
      _zz_141_[31 : 0] = CsrPlugin_minstret[31 : 0];
    end
  end

  always @ (*) begin
    _zz_142_ = 32'h0;
    if(execute_CsrPlugin_csr_2946)begin
      _zz_142_[31 : 0] = CsrPlugin_minstret[63 : 32];
    end
  end

  always @ (*) begin
    _zz_143_ = 32'h0;
    if(execute_CsrPlugin_csr_3072)begin
      _zz_143_[31 : 0] = CsrPlugin_mcycle[31 : 0];
    end
  end

  always @ (*) begin
    _zz_144_ = 32'h0;
    if(execute_CsrPlugin_csr_3200)begin
      _zz_144_[31 : 0] = CsrPlugin_mcycle[63 : 32];
    end
  end

  assign execute_CsrPlugin_readData = (((((_zz_416_ | _zz_417_) | (_zz_418_ | _zz_419_)) | ((_zz_130_ | _zz_131_) | (_zz_132_ | _zz_133_))) | (((_zz_134_ | _zz_135_) | (_zz_136_ | _zz_137_)) | ((_zz_138_ | _zz_139_) | (_zz_140_ | _zz_141_)))) | ((_zz_142_ | _zz_143_) | _zz_144_));
  assign _zz_146_ = 1'b0;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      IBusSimplePlugin_fetchPc_pcReg <= 32'h80000000;
      IBusSimplePlugin_fetchPc_correctionReg <= 1'b0;
      IBusSimplePlugin_fetchPc_booted <= 1'b0;
      IBusSimplePlugin_fetchPc_inc <= 1'b0;
      _zz_55_ <= 1'b0;
      _zz_58_ <= 1'b0;
      _zz_60_ <= 1'b0;
      IBusSimplePlugin_injector_nextPcCalc_valids_0 <= 1'b0;
      IBusSimplePlugin_injector_nextPcCalc_valids_1 <= 1'b0;
      IBusSimplePlugin_injector_nextPcCalc_valids_2 <= 1'b0;
      IBusSimplePlugin_injector_nextPcCalc_valids_3 <= 1'b0;
      IBusSimplePlugin_injector_nextPcCalc_valids_4 <= 1'b0;
      IBusSimplePlugin_injector_nextPcCalc_valids_5 <= 1'b0;
      IBusSimplePlugin_pending_value <= (3'b000);
      IBusSimplePlugin_cmdFork_cmdKeep <= 1'b0;
      IBusSimplePlugin_cmdFork_cmdFired <= 1'b0;
      IBusSimplePlugin_rspJoin_rspBuffer_discardCounter <= (3'b000);
      _zz_87_ <= 1'b1;
      _zz_99_ <= 1'b0;
      memory_DivPlugin_div_counter_value <= 6'h0;
      CsrPlugin_misa_base <= (2'b01);
      CsrPlugin_misa_extensions <= 26'h0001100;
      CsrPlugin_mtvec_mode <= (2'b00);
      CsrPlugin_mtvec_base <= 30'h20000000;
      CsrPlugin_mstatus_MIE <= 1'b0;
      CsrPlugin_mstatus_MPIE <= 1'b0;
      CsrPlugin_mstatus_MPP <= (2'b11);
      CsrPlugin_mie_MEIE <= 1'b0;
      CsrPlugin_mie_MTIE <= 1'b0;
      CsrPlugin_mie_MSIE <= 1'b0;
      CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_decode <= 1'b0;
      CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_execute <= 1'b0;
      CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_memory <= 1'b0;
      CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_writeBack <= 1'b0;
      CsrPlugin_interrupt_valid <= 1'b0;
      CsrPlugin_lastStageWasWfi <= 1'b0;
      CsrPlugin_pipelineLiberator_pcValids_0 <= 1'b0;
      CsrPlugin_pipelineLiberator_pcValids_1 <= 1'b0;
      CsrPlugin_pipelineLiberator_pcValids_2 <= 1'b0;
      CsrPlugin_hadException <= 1'b0;
      execute_CsrPlugin_wfiWake <= 1'b0;
      execute_arbitration_isValid <= 1'b0;
      memory_arbitration_isValid <= 1'b0;
      writeBack_arbitration_isValid <= 1'b0;
      _zz_129_ <= (3'b000);
      memory_to_writeBack_REGFILE_WRITE_DATA <= 32'h0;
      memory_to_writeBack_INSTRUCTION <= 32'h0;
    end else begin
      if(IBusSimplePlugin_fetchPc_correction)begin
        IBusSimplePlugin_fetchPc_correctionReg <= 1'b1;
      end
      if((IBusSimplePlugin_fetchPc_output_valid && IBusSimplePlugin_fetchPc_output_ready))begin
        IBusSimplePlugin_fetchPc_correctionReg <= 1'b0;
      end
      IBusSimplePlugin_fetchPc_booted <= 1'b1;
      if((IBusSimplePlugin_fetchPc_correction || IBusSimplePlugin_fetchPc_pcRegPropagate))begin
        IBusSimplePlugin_fetchPc_inc <= 1'b0;
      end
      if((IBusSimplePlugin_fetchPc_output_valid && IBusSimplePlugin_fetchPc_output_ready))begin
        IBusSimplePlugin_fetchPc_inc <= 1'b1;
      end
      if(((! IBusSimplePlugin_fetchPc_output_valid) && IBusSimplePlugin_fetchPc_output_ready))begin
        IBusSimplePlugin_fetchPc_inc <= 1'b0;
      end
      if((IBusSimplePlugin_fetchPc_booted && ((IBusSimplePlugin_fetchPc_output_ready || IBusSimplePlugin_fetchPc_correction) || IBusSimplePlugin_fetchPc_pcRegPropagate)))begin
        IBusSimplePlugin_fetchPc_pcReg <= IBusSimplePlugin_fetchPc_pc;
      end
      if(IBusSimplePlugin_iBusRsp_flush)begin
        _zz_55_ <= 1'b0;
      end
      if(IBusSimplePlugin_iBusRsp_stages_0_output_ready)begin
        _zz_55_ <= (IBusSimplePlugin_iBusRsp_stages_0_output_valid && (! 1'b0));
      end
      if(IBusSimplePlugin_iBusRsp_flush)begin
        _zz_58_ <= 1'b0;
      end
      if(IBusSimplePlugin_iBusRsp_stages_1_output_ready)begin
        _zz_58_ <= (IBusSimplePlugin_iBusRsp_stages_1_output_valid && (! IBusSimplePlugin_iBusRsp_flush));
      end
      if(decode_arbitration_removeIt)begin
        _zz_60_ <= 1'b0;
      end
      if(IBusSimplePlugin_iBusRsp_output_ready)begin
        _zz_60_ <= (IBusSimplePlugin_iBusRsp_output_valid && (! IBusSimplePlugin_externalFlush));
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_0 <= 1'b0;
      end
      if((! (! IBusSimplePlugin_iBusRsp_stages_1_input_ready)))begin
        IBusSimplePlugin_injector_nextPcCalc_valids_0 <= 1'b1;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_1 <= 1'b0;
      end
      if((! (! IBusSimplePlugin_iBusRsp_stages_2_input_ready)))begin
        IBusSimplePlugin_injector_nextPcCalc_valids_1 <= IBusSimplePlugin_injector_nextPcCalc_valids_0;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_1 <= 1'b0;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_2 <= 1'b0;
      end
      if((! (! IBusSimplePlugin_injector_decodeInput_ready)))begin
        IBusSimplePlugin_injector_nextPcCalc_valids_2 <= IBusSimplePlugin_injector_nextPcCalc_valids_1;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_2 <= 1'b0;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_3 <= 1'b0;
      end
      if((! execute_arbitration_isStuck))begin
        IBusSimplePlugin_injector_nextPcCalc_valids_3 <= IBusSimplePlugin_injector_nextPcCalc_valids_2;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_3 <= 1'b0;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_4 <= 1'b0;
      end
      if((! memory_arbitration_isStuck))begin
        IBusSimplePlugin_injector_nextPcCalc_valids_4 <= IBusSimplePlugin_injector_nextPcCalc_valids_3;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_4 <= 1'b0;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_5 <= 1'b0;
      end
      if((! writeBack_arbitration_isStuck))begin
        IBusSimplePlugin_injector_nextPcCalc_valids_5 <= IBusSimplePlugin_injector_nextPcCalc_valids_4;
      end
      if(IBusSimplePlugin_fetchPc_flushed)begin
        IBusSimplePlugin_injector_nextPcCalc_valids_5 <= 1'b0;
      end
      IBusSimplePlugin_pending_value <= IBusSimplePlugin_pending_next;
      if(IBusSimplePlugin_cmdFork_enterTheMarket)begin
        IBusSimplePlugin_cmdFork_cmdKeep <= 1'b1;
      end
      if(IBusSimplePlugin_cmd_ready)begin
        IBusSimplePlugin_cmdFork_cmdKeep <= 1'b0;
      end
      if((IBusSimplePlugin_cmd_valid && IBusSimplePlugin_cmd_ready))begin
        IBusSimplePlugin_cmdFork_cmdFired <= 1'b1;
      end
      if(IBusSimplePlugin_iBusRsp_stages_1_input_ready)begin
        IBusSimplePlugin_cmdFork_cmdFired <= 1'b0;
      end
      IBusSimplePlugin_rspJoin_rspBuffer_discardCounter <= (IBusSimplePlugin_rspJoin_rspBuffer_discardCounter - _zz_220_);
      if(IBusSimplePlugin_iBusRsp_flush)begin
        IBusSimplePlugin_rspJoin_rspBuffer_discardCounter <= IBusSimplePlugin_pending_next;
      end
      _zz_87_ <= 1'b0;
      _zz_99_ <= (_zz_39_ && writeBack_arbitration_isFiring);
      memory_DivPlugin_div_counter_value <= memory_DivPlugin_div_counter_valueNext;
      if((! decode_arbitration_isStuck))begin
        CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_decode <= 1'b0;
      end else begin
        CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_decode <= CsrPlugin_exceptionPortCtrl_exceptionValids_decode;
      end
      if((! execute_arbitration_isStuck))begin
        CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_execute <= (CsrPlugin_exceptionPortCtrl_exceptionValids_decode && (! decode_arbitration_isStuck));
      end else begin
        CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_execute <= CsrPlugin_exceptionPortCtrl_exceptionValids_execute;
      end
      if((! memory_arbitration_isStuck))begin
        CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_memory <= (CsrPlugin_exceptionPortCtrl_exceptionValids_execute && (! execute_arbitration_isStuck));
      end else begin
        CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_memory <= CsrPlugin_exceptionPortCtrl_exceptionValids_memory;
      end
      if((! writeBack_arbitration_isStuck))begin
        CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_writeBack <= (CsrPlugin_exceptionPortCtrl_exceptionValids_memory && (! memory_arbitration_isStuck));
      end else begin
        CsrPlugin_exceptionPortCtrl_exceptionValidsRegs_writeBack <= 1'b0;
      end
      CsrPlugin_interrupt_valid <= 1'b0;
      if(_zz_175_)begin
        if(_zz_176_)begin
          CsrPlugin_interrupt_valid <= 1'b1;
        end
        if(_zz_177_)begin
          CsrPlugin_interrupt_valid <= 1'b1;
        end
        if(_zz_178_)begin
          CsrPlugin_interrupt_valid <= 1'b1;
        end
      end
      CsrPlugin_lastStageWasWfi <= (writeBack_arbitration_isFiring && (writeBack_ENV_CTRL == `EnvCtrlEnum_defaultEncoding_WFI));
      if(CsrPlugin_pipelineLiberator_active)begin
        if((! execute_arbitration_isStuck))begin
          CsrPlugin_pipelineLiberator_pcValids_0 <= 1'b1;
        end
        if((! memory_arbitration_isStuck))begin
          CsrPlugin_pipelineLiberator_pcValids_1 <= CsrPlugin_pipelineLiberator_pcValids_0;
        end
        if((! writeBack_arbitration_isStuck))begin
          CsrPlugin_pipelineLiberator_pcValids_2 <= CsrPlugin_pipelineLiberator_pcValids_1;
        end
      end
      if(((! CsrPlugin_pipelineLiberator_active) || decode_arbitration_removeIt))begin
        CsrPlugin_pipelineLiberator_pcValids_0 <= 1'b0;
        CsrPlugin_pipelineLiberator_pcValids_1 <= 1'b0;
        CsrPlugin_pipelineLiberator_pcValids_2 <= 1'b0;
      end
      if(CsrPlugin_interruptJump)begin
        CsrPlugin_interrupt_valid <= 1'b0;
      end
      CsrPlugin_hadException <= CsrPlugin_exception;
      if(_zz_159_)begin
        case(CsrPlugin_targetPrivilege)
          2'b11 : begin
            CsrPlugin_mstatus_MIE <= 1'b0;
            CsrPlugin_mstatus_MPIE <= CsrPlugin_mstatus_MIE;
            CsrPlugin_mstatus_MPP <= CsrPlugin_privilege;
          end
          default : begin
          end
        endcase
      end
      if(_zz_160_)begin
        case(_zz_162_)
          2'b11 : begin
            CsrPlugin_mstatus_MPP <= (2'b00);
            CsrPlugin_mstatus_MIE <= CsrPlugin_mstatus_MPIE;
            CsrPlugin_mstatus_MPIE <= 1'b1;
          end
          default : begin
          end
        endcase
      end
      execute_CsrPlugin_wfiWake <= (({_zz_115_,{_zz_114_,_zz_113_}} != (3'b000)) || CsrPlugin_thirdPartyWake);
      if((! writeBack_arbitration_isStuck))begin
        memory_to_writeBack_REGFILE_WRITE_DATA <= _zz_30_;
      end
      if((! writeBack_arbitration_isStuck))begin
        memory_to_writeBack_INSTRUCTION <= memory_INSTRUCTION;
      end
      if(((! execute_arbitration_isStuck) || execute_arbitration_removeIt))begin
        execute_arbitration_isValid <= 1'b0;
      end
      if(((! decode_arbitration_isStuck) && (! decode_arbitration_removeIt)))begin
        execute_arbitration_isValid <= decode_arbitration_isValid;
      end
      if(((! memory_arbitration_isStuck) || memory_arbitration_removeIt))begin
        memory_arbitration_isValid <= 1'b0;
      end
      if(((! execute_arbitration_isStuck) && (! execute_arbitration_removeIt)))begin
        memory_arbitration_isValid <= execute_arbitration_isValid;
      end
      if(((! writeBack_arbitration_isStuck) || writeBack_arbitration_removeIt))begin
        writeBack_arbitration_isValid <= 1'b0;
      end
      if(((! memory_arbitration_isStuck) && (! memory_arbitration_removeIt)))begin
        writeBack_arbitration_isValid <= memory_arbitration_isValid;
      end
      case(_zz_129_)
        3'b000 : begin
          if(IBusSimplePlugin_injectionPort_valid)begin
            _zz_129_ <= (3'b001);
          end
        end
        3'b001 : begin
          _zz_129_ <= (3'b010);
        end
        3'b010 : begin
          _zz_129_ <= (3'b011);
        end
        3'b011 : begin
          if((! decode_arbitration_isStuck))begin
            _zz_129_ <= (3'b100);
          end
        end
        3'b100 : begin
          _zz_129_ <= (3'b000);
        end
        default : begin
        end
      endcase
      if(execute_CsrPlugin_csr_769)begin
        if(execute_CsrPlugin_writeEnable)begin
          CsrPlugin_misa_base <= execute_CsrPlugin_writeData[31 : 30];
          CsrPlugin_misa_extensions <= execute_CsrPlugin_writeData[25 : 0];
        end
      end
      if(execute_CsrPlugin_csr_768)begin
        if(execute_CsrPlugin_writeEnable)begin
          CsrPlugin_mstatus_MPP <= execute_CsrPlugin_writeData[12 : 11];
          CsrPlugin_mstatus_MPIE <= _zz_257_[0];
          CsrPlugin_mstatus_MIE <= _zz_258_[0];
        end
      end
      if(execute_CsrPlugin_csr_772)begin
        if(execute_CsrPlugin_writeEnable)begin
          CsrPlugin_mie_MEIE <= _zz_260_[0];
          CsrPlugin_mie_MTIE <= _zz_261_[0];
          CsrPlugin_mie_MSIE <= _zz_262_[0];
        end
      end
      if(execute_CsrPlugin_csr_773)begin
        if(execute_CsrPlugin_writeEnable)begin
          CsrPlugin_mtvec_base <= execute_CsrPlugin_writeData[31 : 2];
          CsrPlugin_mtvec_mode <= execute_CsrPlugin_writeData[1 : 0];
        end
      end
    end
  end

  always @ (posedge io_clock) begin
    if(IBusSimplePlugin_iBusRsp_stages_0_output_ready)begin
      _zz_56_ <= IBusSimplePlugin_iBusRsp_stages_0_output_payload;
    end
    if(IBusSimplePlugin_iBusRsp_stages_1_output_ready)begin
      _zz_59_ <= IBusSimplePlugin_iBusRsp_stages_1_output_payload;
    end
    if(IBusSimplePlugin_iBusRsp_output_ready)begin
      _zz_61_ <= IBusSimplePlugin_iBusRsp_output_payload_pc;
      _zz_62_ <= IBusSimplePlugin_iBusRsp_output_payload_rsp_error;
      _zz_63_ <= IBusSimplePlugin_iBusRsp_output_payload_rsp_inst;
      _zz_64_ <= IBusSimplePlugin_iBusRsp_output_payload_isRvc;
    end
    if(IBusSimplePlugin_injector_decodeInput_ready)begin
      IBusSimplePlugin_injector_formal_rawInDecode <= IBusSimplePlugin_iBusRsp_output_payload_rsp_inst;
    end
    `ifndef SYNTHESIS
      `ifdef FORMAL
        assert((! (((dBus_rsp_ready && memory_MEMORY_ENABLE) && memory_arbitration_isValid) && memory_arbitration_isStuck)))
      `else
        if(!(! (((dBus_rsp_ready && memory_MEMORY_ENABLE) && memory_arbitration_isValid) && memory_arbitration_isStuck))) begin
          $display("FAILURE DBusSimplePlugin doesn't allow memory stage stall when read happend");
          $finish;
        end
      `endif
    `endif
    `ifndef SYNTHESIS
      `ifdef FORMAL
        assert((! (((writeBack_arbitration_isValid && writeBack_MEMORY_ENABLE) && (! writeBack_MEMORY_STORE)) && writeBack_arbitration_isStuck)))
      `else
        if(!(! (((writeBack_arbitration_isValid && writeBack_MEMORY_ENABLE) && (! writeBack_MEMORY_STORE)) && writeBack_arbitration_isStuck))) begin
          $display("FAILURE DBusSimplePlugin doesn't allow writeback stage stall when read happend");
          $finish;
        end
      `endif
    `endif
    _zz_100_ <= _zz_38_[11 : 7];
    _zz_101_ <= _zz_48_;
    if((memory_DivPlugin_div_counter_value == 6'h20))begin
      memory_DivPlugin_div_done <= 1'b1;
    end
    if((! memory_arbitration_isStuck))begin
      memory_DivPlugin_div_done <= 1'b0;
    end
    if(_zz_149_)begin
      if(_zz_170_)begin
        memory_DivPlugin_rs1[31 : 0] <= memory_DivPlugin_div_stage_0_outNumerator;
        memory_DivPlugin_accumulator[31 : 0] <= memory_DivPlugin_div_stage_0_outRemainder;
        if((memory_DivPlugin_div_counter_value == 6'h20))begin
          memory_DivPlugin_div_result <= _zz_243_[31:0];
        end
      end
    end
    if(_zz_171_)begin
      memory_DivPlugin_accumulator <= 65'h0;
      memory_DivPlugin_rs1 <= ((_zz_111_ ? (~ _zz_112_) : _zz_112_) + _zz_249_);
      memory_DivPlugin_rs2 <= ((_zz_110_ ? (~ execute_RS2) : execute_RS2) + _zz_251_);
      memory_DivPlugin_div_needRevert <= ((_zz_111_ ^ (_zz_110_ && (! execute_INSTRUCTION[13]))) && (! (((execute_RS2 == 32'h0) && execute_IS_RS2_SIGNED) && (! execute_INSTRUCTION[13]))));
    end
    CsrPlugin_mip_MEIP <= externalInterrupt;
    CsrPlugin_mip_MTIP <= timerInterrupt;
    CsrPlugin_mip_MSIP <= softwareInterrupt;
    CsrPlugin_mcycle <= (CsrPlugin_mcycle + 64'h0000000000000001);
    if(writeBack_arbitration_isFiring)begin
      CsrPlugin_minstret <= (CsrPlugin_minstret + 64'h0000000000000001);
    end
    if(decodeExceptionPort_valid)begin
      CsrPlugin_exceptionPortCtrl_exceptionContext_code <= decodeExceptionPort_payload_code;
      CsrPlugin_exceptionPortCtrl_exceptionContext_badAddr <= decodeExceptionPort_payload_badAddr;
    end
    if(_zz_157_)begin
      CsrPlugin_exceptionPortCtrl_exceptionContext_code <= (_zz_117_ ? CsrPlugin_selfException_payload_code : BranchPlugin_branchExceptionPort_payload_code);
      CsrPlugin_exceptionPortCtrl_exceptionContext_badAddr <= (_zz_117_ ? CsrPlugin_selfException_payload_badAddr : BranchPlugin_branchExceptionPort_payload_badAddr);
    end
    if(_zz_175_)begin
      if(_zz_176_)begin
        CsrPlugin_interrupt_code <= (4'b0111);
        CsrPlugin_interrupt_targetPrivilege <= (2'b11);
      end
      if(_zz_177_)begin
        CsrPlugin_interrupt_code <= (4'b0011);
        CsrPlugin_interrupt_targetPrivilege <= (2'b11);
      end
      if(_zz_178_)begin
        CsrPlugin_interrupt_code <= (4'b1011);
        CsrPlugin_interrupt_targetPrivilege <= (2'b11);
      end
    end
    if(_zz_159_)begin
      case(CsrPlugin_targetPrivilege)
        2'b11 : begin
          CsrPlugin_mcause_interrupt <= (! CsrPlugin_hadException);
          CsrPlugin_mcause_exceptionCode <= CsrPlugin_trapCause;
          CsrPlugin_mepc <= writeBack_PC;
          if(CsrPlugin_hadException)begin
            CsrPlugin_mtval <= CsrPlugin_exceptionPortCtrl_exceptionContext_badAddr;
          end
        end
        default : begin
        end
      endcase
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_RS1 <= decode_RS1;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_REGFILE_WRITE_DATA <= _zz_31_;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_SHIFT_CTRL <= _zz_24_;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_MUL_LL <= execute_MUL_LL;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_BRANCH_CTRL <= _zz_21_;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_IS_RS2_SIGNED <= decode_IS_RS2_SIGNED;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_CSR_READ_OPCODE <= decode_CSR_READ_OPCODE;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_SRC1_CTRL <= _zz_18_;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_BYPASSABLE_EXECUTE_STAGE <= decode_BYPASSABLE_EXECUTE_STAGE;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_MUL_HL <= execute_MUL_HL;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_IS_RS1_SIGNED <= decode_IS_RS1_SIGNED;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_MEMORY_ENABLE <= decode_MEMORY_ENABLE;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_MEMORY_ENABLE <= execute_MEMORY_ENABLE;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_MEMORY_ENABLE <= memory_MEMORY_ENABLE;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_ENV_CTRL <= _zz_15_;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_ENV_CTRL <= _zz_12_;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_ENV_CTRL <= _zz_10_;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_ALU_CTRL <= _zz_8_;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_MEMORY_READ_DATA <= memory_MEMORY_READ_DATA;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_SRC_USE_SUB_LESS <= decode_SRC_USE_SUB_LESS;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_INSTRUCTION <= decode_INSTRUCTION;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_INSTRUCTION <= execute_INSTRUCTION;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_MEMORY_ADDRESS_LOW <= execute_MEMORY_ADDRESS_LOW;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_MEMORY_ADDRESS_LOW <= memory_MEMORY_ADDRESS_LOW;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_DO_EBREAK <= decode_DO_EBREAK;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_SRC2_FORCE_ZERO <= decode_SRC2_FORCE_ZERO;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_MEMORY_STORE <= decode_MEMORY_STORE;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_MEMORY_STORE <= execute_MEMORY_STORE;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_MEMORY_STORE <= memory_MEMORY_STORE;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_REGFILE_WRITE_VALID <= decode_REGFILE_WRITE_VALID;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_REGFILE_WRITE_VALID <= execute_REGFILE_WRITE_VALID;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_REGFILE_WRITE_VALID <= memory_REGFILE_WRITE_VALID;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_MUL_LH <= execute_MUL_LH;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_RS2 <= decode_RS2;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_IS_CSR <= decode_IS_CSR;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_IS_DIV <= decode_IS_DIV;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_IS_DIV <= execute_IS_DIV;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_FORMAL_PC_NEXT <= decode_FORMAL_PC_NEXT;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_FORMAL_PC_NEXT <= _zz_49_;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_FORMAL_PC_NEXT <= memory_FORMAL_PC_NEXT;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_MUL_LOW <= memory_MUL_LOW;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_SRC_LESS_UNSIGNED <= decode_SRC_LESS_UNSIGNED;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_CSR_WRITE_OPCODE <= decode_CSR_WRITE_OPCODE;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_BYPASSABLE_MEMORY_STAGE <= decode_BYPASSABLE_MEMORY_STAGE;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_BYPASSABLE_MEMORY_STAGE <= execute_BYPASSABLE_MEMORY_STAGE;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_MUL_HH <= execute_MUL_HH;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_MUL_HH <= memory_MUL_HH;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_IS_MUL <= decode_IS_MUL;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_IS_MUL <= execute_IS_MUL;
    end
    if((! writeBack_arbitration_isStuck))begin
      memory_to_writeBack_IS_MUL <= memory_IS_MUL;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_PC <= decode_PC;
    end
    if((! memory_arbitration_isStuck))begin
      execute_to_memory_PC <= _zz_33_;
    end
    if(((! writeBack_arbitration_isStuck) && (! CsrPlugin_exceptionPortCtrl_exceptionValids_writeBack)))begin
      memory_to_writeBack_PC <= memory_PC;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_SRC2_CTRL <= _zz_5_;
    end
    if((! execute_arbitration_isStuck))begin
      decode_to_execute_ALU_BITWISE_CTRL <= _zz_2_;
    end
    if((_zz_129_ != (3'b000)))begin
      _zz_63_ <= IBusSimplePlugin_injectionPort_payload;
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_3857 <= (decode_INSTRUCTION[31 : 20] == 12'hf11);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_3858 <= (decode_INSTRUCTION[31 : 20] == 12'hf12);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_3859 <= (decode_INSTRUCTION[31 : 20] == 12'hf13);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_3860 <= (decode_INSTRUCTION[31 : 20] == 12'hf14);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_769 <= (decode_INSTRUCTION[31 : 20] == 12'h301);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_768 <= (decode_INSTRUCTION[31 : 20] == 12'h300);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_836 <= (decode_INSTRUCTION[31 : 20] == 12'h344);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_772 <= (decode_INSTRUCTION[31 : 20] == 12'h304);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_773 <= (decode_INSTRUCTION[31 : 20] == 12'h305);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_833 <= (decode_INSTRUCTION[31 : 20] == 12'h341);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_832 <= (decode_INSTRUCTION[31 : 20] == 12'h340);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_834 <= (decode_INSTRUCTION[31 : 20] == 12'h342);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_835 <= (decode_INSTRUCTION[31 : 20] == 12'h343);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_2816 <= (decode_INSTRUCTION[31 : 20] == 12'hb00);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_2944 <= (decode_INSTRUCTION[31 : 20] == 12'hb80);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_2818 <= (decode_INSTRUCTION[31 : 20] == 12'hb02);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_2946 <= (decode_INSTRUCTION[31 : 20] == 12'hb82);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_3072 <= (decode_INSTRUCTION[31 : 20] == 12'hc00);
    end
    if((! execute_arbitration_isStuck))begin
      execute_CsrPlugin_csr_3200 <= (decode_INSTRUCTION[31 : 20] == 12'hc80);
    end
    if(execute_CsrPlugin_csr_836)begin
      if(execute_CsrPlugin_writeEnable)begin
        CsrPlugin_mip_MSIP <= _zz_259_[0];
      end
    end
    if(execute_CsrPlugin_csr_833)begin
      if(execute_CsrPlugin_writeEnable)begin
        CsrPlugin_mepc <= execute_CsrPlugin_writeData[31 : 0];
      end
    end
    if(execute_CsrPlugin_csr_832)begin
      if(execute_CsrPlugin_writeEnable)begin
        CsrPlugin_mscratch <= execute_CsrPlugin_writeData[31 : 0];
      end
    end
    if(execute_CsrPlugin_csr_834)begin
      if(execute_CsrPlugin_writeEnable)begin
        CsrPlugin_mcause_interrupt <= _zz_263_[0];
        CsrPlugin_mcause_exceptionCode <= execute_CsrPlugin_writeData[3 : 0];
      end
    end
    if(execute_CsrPlugin_csr_835)begin
      if(execute_CsrPlugin_writeEnable)begin
        CsrPlugin_mtval <= execute_CsrPlugin_writeData[31 : 0];
      end
    end
    if(execute_CsrPlugin_csr_2816)begin
      if(execute_CsrPlugin_writeEnable)begin
        CsrPlugin_mcycle[31 : 0] <= execute_CsrPlugin_writeData[31 : 0];
      end
    end
    if(execute_CsrPlugin_csr_2944)begin
      if(execute_CsrPlugin_writeEnable)begin
        CsrPlugin_mcycle[63 : 32] <= execute_CsrPlugin_writeData[31 : 0];
      end
    end
    if(execute_CsrPlugin_csr_2818)begin
      if(execute_CsrPlugin_writeEnable)begin
        CsrPlugin_minstret[31 : 0] <= execute_CsrPlugin_writeData[31 : 0];
      end
    end
    if(execute_CsrPlugin_csr_2946)begin
      if(execute_CsrPlugin_writeEnable)begin
        CsrPlugin_minstret[63 : 32] <= execute_CsrPlugin_writeData[31 : 0];
      end
    end
  end

  always @ (posedge io_clock) begin
    DebugPlugin_firstCycle <= 1'b0;
    if(debug_bus_cmd_ready)begin
      DebugPlugin_firstCycle <= 1'b1;
    end
    DebugPlugin_secondCycle <= DebugPlugin_firstCycle;
    DebugPlugin_isPipBusy <= (({writeBack_arbitration_isValid,{memory_arbitration_isValid,{execute_arbitration_isValid,decode_arbitration_isValid}}} != (4'b0000)) || IBusSimplePlugin_incomingInstruction);
    if(writeBack_arbitration_isValid)begin
      DebugPlugin_busReadDataReg <= _zz_48_;
    end
    _zz_128_ <= debug_bus_cmd_payload_address[2];
    if(_zz_156_)begin
      DebugPlugin_busReadDataReg <= execute_PC;
    end
    DebugPlugin_resetIt_regNext <= DebugPlugin_resetIt;
  end

  always @ (posedge io_clock or posedge resetCtrl_debugReset) begin
    if (resetCtrl_debugReset) begin
      DebugPlugin_resetIt <= 1'b0;
      DebugPlugin_haltIt <= 1'b0;
      DebugPlugin_stepIt <= 1'b0;
      DebugPlugin_godmode <= 1'b0;
      DebugPlugin_haltedByBreak <= 1'b0;
    end else begin
      if((DebugPlugin_haltIt && (! DebugPlugin_isPipBusy)))begin
        DebugPlugin_godmode <= 1'b1;
      end
      if(debug_bus_cmd_valid)begin
        case(_zz_174_)
          6'b000000 : begin
            if(debug_bus_cmd_payload_wr)begin
              DebugPlugin_stepIt <= debug_bus_cmd_payload_data[4];
              if(debug_bus_cmd_payload_data[16])begin
                DebugPlugin_resetIt <= 1'b1;
              end
              if(debug_bus_cmd_payload_data[24])begin
                DebugPlugin_resetIt <= 1'b0;
              end
              if(debug_bus_cmd_payload_data[17])begin
                DebugPlugin_haltIt <= 1'b1;
              end
              if(debug_bus_cmd_payload_data[25])begin
                DebugPlugin_haltIt <= 1'b0;
              end
              if(debug_bus_cmd_payload_data[25])begin
                DebugPlugin_haltedByBreak <= 1'b0;
              end
              if(debug_bus_cmd_payload_data[25])begin
                DebugPlugin_godmode <= 1'b0;
              end
            end
          end
          6'b000001 : begin
          end
          default : begin
          end
        endcase
      end
      if(_zz_156_)begin
        if(_zz_158_)begin
          DebugPlugin_haltIt <= 1'b1;
          DebugPlugin_haltedByBreak <= 1'b1;
        end
      end
      if(_zz_161_)begin
        if(decode_arbitration_isValid)begin
          DebugPlugin_haltIt <= 1'b1;
        end
      end
    end
  end


endmodule

module StreamFork_2_ (
  input               io_input_valid,
  output reg          io_input_ready,
  input               io_input_payload_wr,
  input      [31:0]   io_input_payload_address,
  input      [31:0]   io_input_payload_data,
  input      [1:0]    io_input_payload_size,
  output              io_outputs_0_valid,
  input               io_outputs_0_ready,
  output              io_outputs_0_payload_wr,
  output     [31:0]   io_outputs_0_payload_address,
  output     [31:0]   io_outputs_0_payload_data,
  output     [1:0]    io_outputs_0_payload_size,
  output              io_outputs_1_valid,
  input               io_outputs_1_ready,
  output              io_outputs_1_payload_wr,
  output     [31:0]   io_outputs_1_payload_address,
  output     [31:0]   io_outputs_1_payload_data,
  output     [1:0]    io_outputs_1_payload_size,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg                 _zz_1_;
  reg                 _zz_2_;

  always @ (*) begin
    io_input_ready = 1'b1;
    if(((! io_outputs_0_ready) && _zz_1_))begin
      io_input_ready = 1'b0;
    end
    if(((! io_outputs_1_ready) && _zz_2_))begin
      io_input_ready = 1'b0;
    end
  end

  assign io_outputs_0_valid = (io_input_valid && _zz_1_);
  assign io_outputs_0_payload_wr = io_input_payload_wr;
  assign io_outputs_0_payload_address = io_input_payload_address;
  assign io_outputs_0_payload_data = io_input_payload_data;
  assign io_outputs_0_payload_size = io_input_payload_size;
  assign io_outputs_1_valid = (io_input_valid && _zz_2_);
  assign io_outputs_1_payload_wr = io_input_payload_wr;
  assign io_outputs_1_payload_address = io_input_payload_address;
  assign io_outputs_1_payload_data = io_input_payload_data;
  assign io_outputs_1_payload_size = io_input_payload_size;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      _zz_1_ <= 1'b1;
      _zz_2_ <= 1'b1;
    end else begin
      if((io_outputs_0_valid && io_outputs_0_ready))begin
        _zz_1_ <= 1'b0;
      end
      if((io_outputs_1_valid && io_outputs_1_ready))begin
        _zz_2_ <= 1'b0;
      end
      if(io_input_ready)begin
        _zz_1_ <= 1'b1;
        _zz_2_ <= 1'b1;
      end
    end
  end


endmodule

module JtagBridge (
  input               io_jtag_tms,
  input               io_jtag_tdi,
  output              io_jtag_tdo,
  input               io_jtag_tck,
  output              io_remote_cmd_valid,
  input               io_remote_cmd_ready,
  output              io_remote_cmd_payload_last,
  output     [0:0]    io_remote_cmd_payload_fragment,
  input               io_remote_rsp_valid,
  output              io_remote_rsp_ready,
  input               io_remote_rsp_payload_error,
  input      [31:0]   io_remote_rsp_payload_data,
  input               io_clock,
  input               resetCtrl_debugReset 
);
  wire                flowCCByToggle_1__io_output_valid;
  wire                flowCCByToggle_1__io_output_payload_last;
  wire       [0:0]    flowCCByToggle_1__io_output_payload_fragment;
  wire                _zz_2_;
  wire                _zz_3_;
  wire       [0:0]    _zz_4_;
  wire       [3:0]    _zz_5_;
  wire       [1:0]    _zz_6_;
  wire       [3:0]    _zz_7_;
  wire       [1:0]    _zz_8_;
  wire       [3:0]    _zz_9_;
  wire       [0:0]    _zz_10_;
  wire                system_cmd_valid;
  wire                system_cmd_payload_last;
  wire       [0:0]    system_cmd_payload_fragment;
  reg                 system_rsp_valid;
  reg                 system_rsp_payload_error;
  reg        [31:0]   system_rsp_payload_data;
  wire       `JtagState_defaultEncoding_type jtag_tap_fsm_stateNext;
  reg        `JtagState_defaultEncoding_type jtag_tap_fsm_state = `JtagState_defaultEncoding_RESET;
  reg        `JtagState_defaultEncoding_type _zz_1_;
  reg        [3:0]    jtag_tap_instruction;
  reg        [3:0]    jtag_tap_instructionShift;
  reg                 jtag_tap_bypass;
  reg                 jtag_tap_tdoUnbufferd;
  reg                 jtag_tap_tdoUnbufferd_regNext;
  wire                jtag_idcodeArea_instructionHit;
  reg        [31:0]   jtag_idcodeArea_shifter;
  wire                jtag_writeArea_instructionHit;
  reg                 jtag_writeArea_source_valid;
  wire                jtag_writeArea_source_payload_last;
  wire       [0:0]    jtag_writeArea_source_payload_fragment;
  wire                jtag_readArea_instructionHit;
  reg        [33:0]   jtag_readArea_shifter;
  `ifndef SYNTHESIS
  reg [79:0] jtag_tap_fsm_stateNext_string;
  reg [79:0] jtag_tap_fsm_state_string;
  reg [79:0] _zz_1__string;
  `endif


  assign _zz_2_ = (jtag_tap_fsm_state == `JtagState_defaultEncoding_DR_SHIFT);
  assign _zz_3_ = (jtag_tap_fsm_state == `JtagState_defaultEncoding_DR_SHIFT);
  assign _zz_4_ = (1'b1);
  assign _zz_5_ = {3'd0, _zz_4_};
  assign _zz_6_ = (2'b10);
  assign _zz_7_ = {2'd0, _zz_6_};
  assign _zz_8_ = (2'b11);
  assign _zz_9_ = {2'd0, _zz_8_};
  assign _zz_10_ = (1'b1);
  FlowCCByToggle flowCCByToggle_1_ ( 
    .io_input_valid                (jtag_writeArea_source_valid                   ), //i
    .io_input_payload_last         (jtag_writeArea_source_payload_last            ), //i
    .io_input_payload_fragment     (jtag_writeArea_source_payload_fragment        ), //i
    .io_output_valid               (flowCCByToggle_1__io_output_valid             ), //o
    .io_output_payload_last        (flowCCByToggle_1__io_output_payload_last      ), //o
    .io_output_payload_fragment    (flowCCByToggle_1__io_output_payload_fragment  ), //o
    .io_jtag_tck                   (io_jtag_tck                                   ), //i
    .io_clock                      (io_clock                                      ), //i
    .resetCtrl_debugReset          (resetCtrl_debugReset                          )  //i
  );
  `ifndef SYNTHESIS
  always @(*) begin
    case(jtag_tap_fsm_stateNext)
      `JtagState_defaultEncoding_RESET : jtag_tap_fsm_stateNext_string = "RESET     ";
      `JtagState_defaultEncoding_IDLE : jtag_tap_fsm_stateNext_string = "IDLE      ";
      `JtagState_defaultEncoding_IR_SELECT : jtag_tap_fsm_stateNext_string = "IR_SELECT ";
      `JtagState_defaultEncoding_IR_CAPTURE : jtag_tap_fsm_stateNext_string = "IR_CAPTURE";
      `JtagState_defaultEncoding_IR_SHIFT : jtag_tap_fsm_stateNext_string = "IR_SHIFT  ";
      `JtagState_defaultEncoding_IR_EXIT1 : jtag_tap_fsm_stateNext_string = "IR_EXIT1  ";
      `JtagState_defaultEncoding_IR_PAUSE : jtag_tap_fsm_stateNext_string = "IR_PAUSE  ";
      `JtagState_defaultEncoding_IR_EXIT2 : jtag_tap_fsm_stateNext_string = "IR_EXIT2  ";
      `JtagState_defaultEncoding_IR_UPDATE : jtag_tap_fsm_stateNext_string = "IR_UPDATE ";
      `JtagState_defaultEncoding_DR_SELECT : jtag_tap_fsm_stateNext_string = "DR_SELECT ";
      `JtagState_defaultEncoding_DR_CAPTURE : jtag_tap_fsm_stateNext_string = "DR_CAPTURE";
      `JtagState_defaultEncoding_DR_SHIFT : jtag_tap_fsm_stateNext_string = "DR_SHIFT  ";
      `JtagState_defaultEncoding_DR_EXIT1 : jtag_tap_fsm_stateNext_string = "DR_EXIT1  ";
      `JtagState_defaultEncoding_DR_PAUSE : jtag_tap_fsm_stateNext_string = "DR_PAUSE  ";
      `JtagState_defaultEncoding_DR_EXIT2 : jtag_tap_fsm_stateNext_string = "DR_EXIT2  ";
      `JtagState_defaultEncoding_DR_UPDATE : jtag_tap_fsm_stateNext_string = "DR_UPDATE ";
      default : jtag_tap_fsm_stateNext_string = "??????????";
    endcase
  end
  always @(*) begin
    case(jtag_tap_fsm_state)
      `JtagState_defaultEncoding_RESET : jtag_tap_fsm_state_string = "RESET     ";
      `JtagState_defaultEncoding_IDLE : jtag_tap_fsm_state_string = "IDLE      ";
      `JtagState_defaultEncoding_IR_SELECT : jtag_tap_fsm_state_string = "IR_SELECT ";
      `JtagState_defaultEncoding_IR_CAPTURE : jtag_tap_fsm_state_string = "IR_CAPTURE";
      `JtagState_defaultEncoding_IR_SHIFT : jtag_tap_fsm_state_string = "IR_SHIFT  ";
      `JtagState_defaultEncoding_IR_EXIT1 : jtag_tap_fsm_state_string = "IR_EXIT1  ";
      `JtagState_defaultEncoding_IR_PAUSE : jtag_tap_fsm_state_string = "IR_PAUSE  ";
      `JtagState_defaultEncoding_IR_EXIT2 : jtag_tap_fsm_state_string = "IR_EXIT2  ";
      `JtagState_defaultEncoding_IR_UPDATE : jtag_tap_fsm_state_string = "IR_UPDATE ";
      `JtagState_defaultEncoding_DR_SELECT : jtag_tap_fsm_state_string = "DR_SELECT ";
      `JtagState_defaultEncoding_DR_CAPTURE : jtag_tap_fsm_state_string = "DR_CAPTURE";
      `JtagState_defaultEncoding_DR_SHIFT : jtag_tap_fsm_state_string = "DR_SHIFT  ";
      `JtagState_defaultEncoding_DR_EXIT1 : jtag_tap_fsm_state_string = "DR_EXIT1  ";
      `JtagState_defaultEncoding_DR_PAUSE : jtag_tap_fsm_state_string = "DR_PAUSE  ";
      `JtagState_defaultEncoding_DR_EXIT2 : jtag_tap_fsm_state_string = "DR_EXIT2  ";
      `JtagState_defaultEncoding_DR_UPDATE : jtag_tap_fsm_state_string = "DR_UPDATE ";
      default : jtag_tap_fsm_state_string = "??????????";
    endcase
  end
  always @(*) begin
    case(_zz_1_)
      `JtagState_defaultEncoding_RESET : _zz_1__string = "RESET     ";
      `JtagState_defaultEncoding_IDLE : _zz_1__string = "IDLE      ";
      `JtagState_defaultEncoding_IR_SELECT : _zz_1__string = "IR_SELECT ";
      `JtagState_defaultEncoding_IR_CAPTURE : _zz_1__string = "IR_CAPTURE";
      `JtagState_defaultEncoding_IR_SHIFT : _zz_1__string = "IR_SHIFT  ";
      `JtagState_defaultEncoding_IR_EXIT1 : _zz_1__string = "IR_EXIT1  ";
      `JtagState_defaultEncoding_IR_PAUSE : _zz_1__string = "IR_PAUSE  ";
      `JtagState_defaultEncoding_IR_EXIT2 : _zz_1__string = "IR_EXIT2  ";
      `JtagState_defaultEncoding_IR_UPDATE : _zz_1__string = "IR_UPDATE ";
      `JtagState_defaultEncoding_DR_SELECT : _zz_1__string = "DR_SELECT ";
      `JtagState_defaultEncoding_DR_CAPTURE : _zz_1__string = "DR_CAPTURE";
      `JtagState_defaultEncoding_DR_SHIFT : _zz_1__string = "DR_SHIFT  ";
      `JtagState_defaultEncoding_DR_EXIT1 : _zz_1__string = "DR_EXIT1  ";
      `JtagState_defaultEncoding_DR_PAUSE : _zz_1__string = "DR_PAUSE  ";
      `JtagState_defaultEncoding_DR_EXIT2 : _zz_1__string = "DR_EXIT2  ";
      `JtagState_defaultEncoding_DR_UPDATE : _zz_1__string = "DR_UPDATE ";
      default : _zz_1__string = "??????????";
    endcase
  end
  `endif

  assign io_remote_cmd_valid = system_cmd_valid;
  assign io_remote_cmd_payload_last = system_cmd_payload_last;
  assign io_remote_cmd_payload_fragment = system_cmd_payload_fragment;
  assign io_remote_rsp_ready = 1'b1;
  always @ (*) begin
    case(jtag_tap_fsm_state)
      `JtagState_defaultEncoding_IDLE : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_DR_SELECT : `JtagState_defaultEncoding_IDLE);
      end
      `JtagState_defaultEncoding_IR_SELECT : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_RESET : `JtagState_defaultEncoding_IR_CAPTURE);
      end
      `JtagState_defaultEncoding_IR_CAPTURE : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_IR_EXIT1 : `JtagState_defaultEncoding_IR_SHIFT);
      end
      `JtagState_defaultEncoding_IR_SHIFT : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_IR_EXIT1 : `JtagState_defaultEncoding_IR_SHIFT);
      end
      `JtagState_defaultEncoding_IR_EXIT1 : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_IR_UPDATE : `JtagState_defaultEncoding_IR_PAUSE);
      end
      `JtagState_defaultEncoding_IR_PAUSE : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_IR_EXIT2 : `JtagState_defaultEncoding_IR_PAUSE);
      end
      `JtagState_defaultEncoding_IR_EXIT2 : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_IR_UPDATE : `JtagState_defaultEncoding_IR_SHIFT);
      end
      `JtagState_defaultEncoding_IR_UPDATE : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_DR_SELECT : `JtagState_defaultEncoding_IDLE);
      end
      `JtagState_defaultEncoding_DR_SELECT : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_IR_SELECT : `JtagState_defaultEncoding_DR_CAPTURE);
      end
      `JtagState_defaultEncoding_DR_CAPTURE : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_DR_EXIT1 : `JtagState_defaultEncoding_DR_SHIFT);
      end
      `JtagState_defaultEncoding_DR_SHIFT : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_DR_EXIT1 : `JtagState_defaultEncoding_DR_SHIFT);
      end
      `JtagState_defaultEncoding_DR_EXIT1 : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_DR_UPDATE : `JtagState_defaultEncoding_DR_PAUSE);
      end
      `JtagState_defaultEncoding_DR_PAUSE : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_DR_EXIT2 : `JtagState_defaultEncoding_DR_PAUSE);
      end
      `JtagState_defaultEncoding_DR_EXIT2 : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_DR_UPDATE : `JtagState_defaultEncoding_DR_SHIFT);
      end
      `JtagState_defaultEncoding_DR_UPDATE : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_DR_SELECT : `JtagState_defaultEncoding_IDLE);
      end
      default : begin
        _zz_1_ = (io_jtag_tms ? `JtagState_defaultEncoding_RESET : `JtagState_defaultEncoding_IDLE);
      end
    endcase
  end

  assign jtag_tap_fsm_stateNext = _zz_1_;
  always @ (*) begin
    jtag_tap_tdoUnbufferd = jtag_tap_bypass;
    case(jtag_tap_fsm_state)
      `JtagState_defaultEncoding_IR_CAPTURE : begin
      end
      `JtagState_defaultEncoding_IR_SHIFT : begin
        jtag_tap_tdoUnbufferd = jtag_tap_instructionShift[0];
      end
      `JtagState_defaultEncoding_IR_UPDATE : begin
      end
      default : begin
      end
    endcase
    if(jtag_idcodeArea_instructionHit)begin
      if(_zz_2_)begin
        jtag_tap_tdoUnbufferd = jtag_idcodeArea_shifter[0];
      end
    end
    if(jtag_readArea_instructionHit)begin
      if(_zz_3_)begin
        jtag_tap_tdoUnbufferd = jtag_readArea_shifter[0];
      end
    end
  end

  assign io_jtag_tdo = jtag_tap_tdoUnbufferd_regNext;
  assign jtag_idcodeArea_instructionHit = (jtag_tap_instruction == _zz_5_);
  assign jtag_writeArea_instructionHit = (jtag_tap_instruction == _zz_7_);
  always @ (*) begin
    jtag_writeArea_source_valid = 1'b0;
    if(jtag_writeArea_instructionHit)begin
      if((jtag_tap_fsm_state == `JtagState_defaultEncoding_DR_SHIFT))begin
        jtag_writeArea_source_valid = 1'b1;
      end
    end
  end

  assign jtag_writeArea_source_payload_last = io_jtag_tms;
  assign jtag_writeArea_source_payload_fragment[0] = io_jtag_tdi;
  assign system_cmd_valid = flowCCByToggle_1__io_output_valid;
  assign system_cmd_payload_last = flowCCByToggle_1__io_output_payload_last;
  assign system_cmd_payload_fragment = flowCCByToggle_1__io_output_payload_fragment;
  assign jtag_readArea_instructionHit = (jtag_tap_instruction == _zz_9_);
  always @ (posedge io_clock) begin
    if(io_remote_cmd_valid)begin
      system_rsp_valid <= 1'b0;
    end
    if((io_remote_rsp_valid && io_remote_rsp_ready))begin
      system_rsp_valid <= 1'b1;
      system_rsp_payload_error <= io_remote_rsp_payload_error;
      system_rsp_payload_data <= io_remote_rsp_payload_data;
    end
  end

  always @ (posedge io_jtag_tck) begin
    jtag_tap_fsm_state <= jtag_tap_fsm_stateNext;
    jtag_tap_bypass <= io_jtag_tdi;
    case(jtag_tap_fsm_state)
      `JtagState_defaultEncoding_IR_CAPTURE : begin
        jtag_tap_instructionShift <= jtag_tap_instruction;
      end
      `JtagState_defaultEncoding_IR_SHIFT : begin
        jtag_tap_instructionShift <= ({io_jtag_tdi,jtag_tap_instructionShift} >>> 1);
      end
      `JtagState_defaultEncoding_IR_UPDATE : begin
        jtag_tap_instruction <= jtag_tap_instructionShift;
      end
      default : begin
      end
    endcase
    if(jtag_idcodeArea_instructionHit)begin
      if(_zz_2_)begin
        jtag_idcodeArea_shifter <= ({io_jtag_tdi,jtag_idcodeArea_shifter} >>> 1);
      end
    end
    if((jtag_tap_fsm_state == `JtagState_defaultEncoding_RESET))begin
      jtag_idcodeArea_shifter <= 32'h10001fff;
      jtag_tap_instruction <= {3'd0, _zz_10_};
    end
    if(jtag_readArea_instructionHit)begin
      if((jtag_tap_fsm_state == `JtagState_defaultEncoding_DR_CAPTURE))begin
        jtag_readArea_shifter <= {{system_rsp_payload_data,system_rsp_payload_error},system_rsp_valid};
      end
      if(_zz_3_)begin
        jtag_readArea_shifter <= ({io_jtag_tdi,jtag_readArea_shifter} >>> 1);
      end
    end
  end

  always @ (negedge io_jtag_tck) begin
    jtag_tap_tdoUnbufferd_regNext <= jtag_tap_tdoUnbufferd;
  end


endmodule

module SystemDebugger (
  input               io_remote_cmd_valid,
  output              io_remote_cmd_ready,
  input               io_remote_cmd_payload_last,
  input      [0:0]    io_remote_cmd_payload_fragment,
  output              io_remote_rsp_valid,
  input               io_remote_rsp_ready,
  output              io_remote_rsp_payload_error,
  output     [31:0]   io_remote_rsp_payload_data,
  output              io_mem_cmd_valid,
  input               io_mem_cmd_ready,
  output     [31:0]   io_mem_cmd_payload_address,
  output     [31:0]   io_mem_cmd_payload_data,
  output              io_mem_cmd_payload_wr,
  output     [1:0]    io_mem_cmd_payload_size,
  input               io_mem_rsp_valid,
  input      [31:0]   io_mem_rsp_payload,
  input               io_clock,
  input               resetCtrl_debugReset 
);
  wire                _zz_2_;
  wire       [0:0]    _zz_3_;
  reg        [66:0]   dispatcher_dataShifter;
  reg                 dispatcher_dataLoaded;
  reg        [7:0]    dispatcher_headerShifter;
  wire       [7:0]    dispatcher_header;
  reg                 dispatcher_headerLoaded;
  reg        [2:0]    dispatcher_counter;
  wire       [66:0]   _zz_1_;

  assign _zz_2_ = (dispatcher_headerLoaded == 1'b0);
  assign _zz_3_ = _zz_1_[64 : 64];
  assign dispatcher_header = dispatcher_headerShifter[7 : 0];
  assign io_remote_cmd_ready = (! dispatcher_dataLoaded);
  assign _zz_1_ = dispatcher_dataShifter[66 : 0];
  assign io_mem_cmd_payload_address = _zz_1_[31 : 0];
  assign io_mem_cmd_payload_data = _zz_1_[63 : 32];
  assign io_mem_cmd_payload_wr = _zz_3_[0];
  assign io_mem_cmd_payload_size = _zz_1_[66 : 65];
  assign io_mem_cmd_valid = (dispatcher_dataLoaded && (dispatcher_header == 8'h0));
  assign io_remote_rsp_valid = io_mem_rsp_valid;
  assign io_remote_rsp_payload_error = 1'b0;
  assign io_remote_rsp_payload_data = io_mem_rsp_payload;
  always @ (posedge io_clock or posedge resetCtrl_debugReset) begin
    if (resetCtrl_debugReset) begin
      dispatcher_dataLoaded <= 1'b0;
      dispatcher_headerLoaded <= 1'b0;
      dispatcher_counter <= (3'b000);
    end else begin
      if(io_remote_cmd_valid)begin
        if(_zz_2_)begin
          dispatcher_counter <= (dispatcher_counter + (3'b001));
          if((dispatcher_counter == (3'b111)))begin
            dispatcher_headerLoaded <= 1'b1;
          end
        end
        if(io_remote_cmd_payload_last)begin
          dispatcher_headerLoaded <= 1'b1;
          dispatcher_dataLoaded <= 1'b1;
          dispatcher_counter <= (3'b000);
        end
      end
      if((io_mem_cmd_valid && io_mem_cmd_ready))begin
        dispatcher_headerLoaded <= 1'b0;
        dispatcher_dataLoaded <= 1'b0;
      end
    end
  end

  always @ (posedge io_clock) begin
    if(io_remote_cmd_valid)begin
      if(_zz_2_)begin
        dispatcher_headerShifter <= ({io_remote_cmd_payload_fragment,dispatcher_headerShifter} >>> 1);
      end else begin
        dispatcher_dataShifter <= ({io_remote_cmd_payload_fragment,dispatcher_dataShifter} >>> 1);
      end
    end
  end


endmodule

module Axi4SharedOnChipRam (
  input               io_axi_arw_valid,
  output reg          io_axi_arw_ready,
  input      [16:0]   io_axi_arw_payload_addr,
  input      [3:0]    io_axi_arw_payload_id,
  input      [7:0]    io_axi_arw_payload_len,
  input      [2:0]    io_axi_arw_payload_size,
  input      [1:0]    io_axi_arw_payload_burst,
  input               io_axi_arw_payload_write,
  input               io_axi_w_valid,
  output              io_axi_w_ready,
  input      [31:0]   io_axi_w_payload_data,
  input      [3:0]    io_axi_w_payload_strb,
  input               io_axi_w_payload_last,
  output              io_axi_b_valid,
  input               io_axi_b_ready,
  output     [3:0]    io_axi_b_payload_id,
  output     [1:0]    io_axi_b_payload_resp,
  output              io_axi_r_valid,
  input               io_axi_r_ready,
  output     [31:0]   io_axi_r_payload_data,
  output     [3:0]    io_axi_r_payload_id,
  output     [1:0]    io_axi_r_payload_resp,
  output              io_axi_r_payload_last,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [31:0]   _zz_6_;
  reg        [11:0]   _zz_7_;
  wire                _zz_8_;
  wire       [1:0]    _zz_9_;
  wire       [11:0]   _zz_10_;
  wire       [11:0]   _zz_11_;
  wire       [11:0]   _zz_12_;
  wire       [2:0]    _zz_13_;
  wire       [2:0]    _zz_14_;
  reg                 unburstify_result_valid;
  wire                unburstify_result_ready;
  reg                 unburstify_result_payload_last;
  reg        [16:0]   unburstify_result_payload_fragment_addr;
  reg        [3:0]    unburstify_result_payload_fragment_id;
  reg        [2:0]    unburstify_result_payload_fragment_size;
  reg        [1:0]    unburstify_result_payload_fragment_burst;
  reg                 unburstify_result_payload_fragment_write;
  wire                unburstify_doResult;
  reg                 unburstify_buffer_valid;
  reg        [7:0]    unburstify_buffer_len;
  reg        [7:0]    unburstify_buffer_beat;
  reg        [16:0]   unburstify_buffer_transaction_addr;
  reg        [3:0]    unburstify_buffer_transaction_id;
  reg        [2:0]    unburstify_buffer_transaction_size;
  reg        [1:0]    unburstify_buffer_transaction_burst;
  reg                 unburstify_buffer_transaction_write;
  wire                unburstify_buffer_last;
  wire       [1:0]    Axi4Incr_validSize;
  reg        [16:0]   Axi4Incr_result;
  wire       [4:0]    Axi4Incr_highCat;
  wire       [2:0]    Axi4Incr_sizeValue;
  wire       [11:0]   Axi4Incr_alignMask;
  wire       [11:0]   Axi4Incr_base;
  wire       [11:0]   Axi4Incr_baseIncr;
  reg        [1:0]    _zz_1_;
  wire       [2:0]    Axi4Incr_wrapCase;
  wire                _zz_2_;
  wire                stage0_valid;
  wire                stage0_ready;
  wire                stage0_payload_last;
  wire       [16:0]   stage0_payload_fragment_addr;
  wire       [3:0]    stage0_payload_fragment_id;
  wire       [2:0]    stage0_payload_fragment_size;
  wire       [1:0]    stage0_payload_fragment_burst;
  wire                stage0_payload_fragment_write;
  wire       [14:0]   _zz_3_;
  wire                _zz_4_;
  wire       [31:0]   _zz_5_;
  wire                stage1_valid;
  wire                stage1_ready;
  wire                stage1_payload_last;
  wire       [16:0]   stage1_payload_fragment_addr;
  wire       [3:0]    stage1_payload_fragment_id;
  wire       [2:0]    stage1_payload_fragment_size;
  wire       [1:0]    stage1_payload_fragment_burst;
  wire                stage1_payload_fragment_write;
  reg                 stage0_m2sPipe_rValid;
  reg                 stage0_m2sPipe_rData_last;
  reg        [16:0]   stage0_m2sPipe_rData_fragment_addr;
  reg        [3:0]    stage0_m2sPipe_rData_fragment_id;
  reg        [2:0]    stage0_m2sPipe_rData_fragment_size;
  reg        [1:0]    stage0_m2sPipe_rData_fragment_burst;
  reg                 stage0_m2sPipe_rData_fragment_write;
  reg [7:0] ram_symbol0 [0:32767];
  reg [7:0] ram_symbol1 [0:32767];
  reg [7:0] ram_symbol2 [0:32767];
  reg [7:0] ram_symbol3 [0:32767];
  reg [7:0] _zz_15_;
  reg [7:0] _zz_16_;
  reg [7:0] _zz_17_;
  reg [7:0] _zz_18_;

  assign _zz_8_ = (io_axi_arw_payload_len == 8'h0);
  assign _zz_9_ = {((2'b01) < Axi4Incr_validSize),((2'b00) < Axi4Incr_validSize)};
  assign _zz_10_ = unburstify_buffer_transaction_addr[11 : 0];
  assign _zz_11_ = _zz_10_;
  assign _zz_12_ = {9'd0, Axi4Incr_sizeValue};
  assign _zz_13_ = {1'd0, Axi4Incr_validSize};
  assign _zz_14_ = {1'd0, _zz_1_};
  initial begin
    $readmemb("Hydrogen.v_toplevel_system_onChipRam_ram_symbol0.bin",ram_symbol0);
    $readmemb("Hydrogen.v_toplevel_system_onChipRam_ram_symbol1.bin",ram_symbol1);
    $readmemb("Hydrogen.v_toplevel_system_onChipRam_ram_symbol2.bin",ram_symbol2);
    $readmemb("Hydrogen.v_toplevel_system_onChipRam_ram_symbol3.bin",ram_symbol3);
  end
  always @ (*) begin
    _zz_6_ = {_zz_18_, _zz_17_, _zz_16_, _zz_15_};
  end
  always @ (posedge io_clock) begin
    if(_zz_4_) begin
      _zz_15_ <= ram_symbol0[_zz_3_];
      _zz_16_ <= ram_symbol1[_zz_3_];
      _zz_17_ <= ram_symbol2[_zz_3_];
      _zz_18_ <= ram_symbol3[_zz_3_];
    end
  end

  always @ (posedge io_clock) begin
    if(io_axi_w_payload_strb[0] && _zz_4_ && stage0_payload_fragment_write ) begin
      ram_symbol0[_zz_3_] <= _zz_5_[7 : 0];
    end
    if(io_axi_w_payload_strb[1] && _zz_4_ && stage0_payload_fragment_write ) begin
      ram_symbol1[_zz_3_] <= _zz_5_[15 : 8];
    end
    if(io_axi_w_payload_strb[2] && _zz_4_ && stage0_payload_fragment_write ) begin
      ram_symbol2[_zz_3_] <= _zz_5_[23 : 16];
    end
    if(io_axi_w_payload_strb[3] && _zz_4_ && stage0_payload_fragment_write ) begin
      ram_symbol3[_zz_3_] <= _zz_5_[31 : 24];
    end
  end

  always @(*) begin
    case(Axi4Incr_wrapCase)
      3'b000 : begin
        _zz_7_ = {Axi4Incr_base[11 : 1],Axi4Incr_baseIncr[0 : 0]};
      end
      3'b001 : begin
        _zz_7_ = {Axi4Incr_base[11 : 2],Axi4Incr_baseIncr[1 : 0]};
      end
      3'b010 : begin
        _zz_7_ = {Axi4Incr_base[11 : 3],Axi4Incr_baseIncr[2 : 0]};
      end
      3'b011 : begin
        _zz_7_ = {Axi4Incr_base[11 : 4],Axi4Incr_baseIncr[3 : 0]};
      end
      3'b100 : begin
        _zz_7_ = {Axi4Incr_base[11 : 5],Axi4Incr_baseIncr[4 : 0]};
      end
      default : begin
        _zz_7_ = {Axi4Incr_base[11 : 6],Axi4Incr_baseIncr[5 : 0]};
      end
    endcase
  end

  assign unburstify_buffer_last = (unburstify_buffer_beat == 8'h01);
  assign Axi4Incr_validSize = unburstify_buffer_transaction_size[1 : 0];
  assign Axi4Incr_highCat = unburstify_buffer_transaction_addr[16 : 12];
  assign Axi4Incr_sizeValue = {((2'b10) == Axi4Incr_validSize),{((2'b01) == Axi4Incr_validSize),((2'b00) == Axi4Incr_validSize)}};
  assign Axi4Incr_alignMask = {10'd0, _zz_9_};
  assign Axi4Incr_base = (_zz_11_ & (~ Axi4Incr_alignMask));
  assign Axi4Incr_baseIncr = (Axi4Incr_base + _zz_12_);
  always @ (*) begin
    if((((unburstify_buffer_len & 8'h08) == 8'h08))) begin
        _zz_1_ = (2'b11);
    end else if((((unburstify_buffer_len & 8'h04) == 8'h04))) begin
        _zz_1_ = (2'b10);
    end else if((((unburstify_buffer_len & 8'h02) == 8'h02))) begin
        _zz_1_ = (2'b01);
    end else begin
        _zz_1_ = (2'b00);
    end
  end

  assign Axi4Incr_wrapCase = (_zz_13_ + _zz_14_);
  always @ (*) begin
    case(unburstify_buffer_transaction_burst)
      2'b00 : begin
        Axi4Incr_result = unburstify_buffer_transaction_addr;
      end
      2'b10 : begin
        Axi4Incr_result = {Axi4Incr_highCat,_zz_7_};
      end
      default : begin
        Axi4Incr_result = {Axi4Incr_highCat,Axi4Incr_baseIncr};
      end
    endcase
  end

  always @ (*) begin
    io_axi_arw_ready = 1'b0;
    if(! unburstify_buffer_valid) begin
      io_axi_arw_ready = unburstify_result_ready;
    end
  end

  always @ (*) begin
    if(unburstify_buffer_valid)begin
      unburstify_result_valid = 1'b1;
    end else begin
      unburstify_result_valid = io_axi_arw_valid;
    end
  end

  always @ (*) begin
    if(unburstify_buffer_valid)begin
      unburstify_result_payload_last = unburstify_buffer_last;
    end else begin
      if(_zz_8_)begin
        unburstify_result_payload_last = 1'b1;
      end else begin
        unburstify_result_payload_last = 1'b0;
      end
    end
  end

  always @ (*) begin
    if(unburstify_buffer_valid)begin
      unburstify_result_payload_fragment_id = unburstify_buffer_transaction_id;
    end else begin
      unburstify_result_payload_fragment_id = io_axi_arw_payload_id;
    end
  end

  always @ (*) begin
    if(unburstify_buffer_valid)begin
      unburstify_result_payload_fragment_size = unburstify_buffer_transaction_size;
    end else begin
      unburstify_result_payload_fragment_size = io_axi_arw_payload_size;
    end
  end

  always @ (*) begin
    if(unburstify_buffer_valid)begin
      unburstify_result_payload_fragment_burst = unburstify_buffer_transaction_burst;
    end else begin
      unburstify_result_payload_fragment_burst = io_axi_arw_payload_burst;
    end
  end

  always @ (*) begin
    if(unburstify_buffer_valid)begin
      unburstify_result_payload_fragment_write = unburstify_buffer_transaction_write;
    end else begin
      unburstify_result_payload_fragment_write = io_axi_arw_payload_write;
    end
  end

  always @ (*) begin
    if(unburstify_buffer_valid)begin
      unburstify_result_payload_fragment_addr = Axi4Incr_result;
    end else begin
      unburstify_result_payload_fragment_addr = io_axi_arw_payload_addr;
    end
  end

  assign _zz_2_ = (! (unburstify_result_payload_fragment_write && (! io_axi_w_valid)));
  assign stage0_valid = (unburstify_result_valid && _zz_2_);
  assign unburstify_result_ready = (stage0_ready && _zz_2_);
  assign stage0_payload_last = unburstify_result_payload_last;
  assign stage0_payload_fragment_addr = unburstify_result_payload_fragment_addr;
  assign stage0_payload_fragment_id = unburstify_result_payload_fragment_id;
  assign stage0_payload_fragment_size = unburstify_result_payload_fragment_size;
  assign stage0_payload_fragment_burst = unburstify_result_payload_fragment_burst;
  assign stage0_payload_fragment_write = unburstify_result_payload_fragment_write;
  assign _zz_3_ = stage0_payload_fragment_addr[16 : 2];
  assign _zz_4_ = (stage0_valid && stage0_ready);
  assign _zz_5_ = io_axi_w_payload_data;
  assign io_axi_r_payload_data = _zz_6_;
  assign io_axi_w_ready = ((unburstify_result_valid && unburstify_result_payload_fragment_write) && stage0_ready);
  assign stage0_ready = ((1'b1 && (! stage1_valid)) || stage1_ready);
  assign stage1_valid = stage0_m2sPipe_rValid;
  assign stage1_payload_last = stage0_m2sPipe_rData_last;
  assign stage1_payload_fragment_addr = stage0_m2sPipe_rData_fragment_addr;
  assign stage1_payload_fragment_id = stage0_m2sPipe_rData_fragment_id;
  assign stage1_payload_fragment_size = stage0_m2sPipe_rData_fragment_size;
  assign stage1_payload_fragment_burst = stage0_m2sPipe_rData_fragment_burst;
  assign stage1_payload_fragment_write = stage0_m2sPipe_rData_fragment_write;
  assign stage1_ready = ((io_axi_r_ready && (! stage1_payload_fragment_write)) || ((io_axi_b_ready || (! stage1_payload_last)) && stage1_payload_fragment_write));
  assign io_axi_r_valid = (stage1_valid && (! stage1_payload_fragment_write));
  assign io_axi_r_payload_id = stage1_payload_fragment_id;
  assign io_axi_r_payload_last = stage1_payload_last;
  assign io_axi_r_payload_resp = (2'b00);
  assign io_axi_b_valid = ((stage1_valid && stage1_payload_fragment_write) && stage1_payload_last);
  assign io_axi_b_payload_resp = (2'b00);
  assign io_axi_b_payload_id = stage1_payload_fragment_id;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      unburstify_buffer_valid <= 1'b0;
      stage0_m2sPipe_rValid <= 1'b0;
    end else begin
      if(unburstify_result_ready)begin
        if(unburstify_buffer_last)begin
          unburstify_buffer_valid <= 1'b0;
        end
      end
      if(! unburstify_buffer_valid) begin
        if(! _zz_8_) begin
          if(unburstify_result_ready)begin
            unburstify_buffer_valid <= io_axi_arw_valid;
          end
        end
      end
      if(stage0_ready)begin
        stage0_m2sPipe_rValid <= stage0_valid;
      end
    end
  end

  always @ (posedge io_clock) begin
    if(unburstify_result_ready)begin
      unburstify_buffer_beat <= (unburstify_buffer_beat - 8'h01);
      unburstify_buffer_transaction_addr[11 : 0] <= Axi4Incr_result[11 : 0];
    end
    if(! unburstify_buffer_valid) begin
      if(! _zz_8_) begin
        if(unburstify_result_ready)begin
          unburstify_buffer_transaction_addr <= io_axi_arw_payload_addr;
          unburstify_buffer_transaction_id <= io_axi_arw_payload_id;
          unburstify_buffer_transaction_size <= io_axi_arw_payload_size;
          unburstify_buffer_transaction_burst <= io_axi_arw_payload_burst;
          unburstify_buffer_transaction_write <= io_axi_arw_payload_write;
          unburstify_buffer_beat <= io_axi_arw_payload_len;
          unburstify_buffer_len <= io_axi_arw_payload_len;
        end
      end
    end
    if(stage0_ready)begin
      stage0_m2sPipe_rData_last <= stage0_payload_last;
      stage0_m2sPipe_rData_fragment_addr <= stage0_payload_fragment_addr;
      stage0_m2sPipe_rData_fragment_id <= stage0_payload_fragment_id;
      stage0_m2sPipe_rData_fragment_size <= stage0_payload_fragment_size;
      stage0_m2sPipe_rData_fragment_burst <= stage0_payload_fragment_burst;
      stage0_m2sPipe_rData_fragment_write <= stage0_payload_fragment_write;
    end
  end


endmodule

module Axi4SharedToApb3Bridge (
  input               io_axi_arw_valid,
  output reg          io_axi_arw_ready,
  input      [19:0]   io_axi_arw_payload_addr,
  input      [3:0]    io_axi_arw_payload_id,
  input      [7:0]    io_axi_arw_payload_len,
  input      [2:0]    io_axi_arw_payload_size,
  input      [1:0]    io_axi_arw_payload_burst,
  input               io_axi_arw_payload_write,
  input               io_axi_w_valid,
  output reg          io_axi_w_ready,
  input      [31:0]   io_axi_w_payload_data,
  input      [3:0]    io_axi_w_payload_strb,
  input               io_axi_w_payload_last,
  output reg          io_axi_b_valid,
  input               io_axi_b_ready,
  output     [3:0]    io_axi_b_payload_id,
  output     [1:0]    io_axi_b_payload_resp,
  output reg          io_axi_r_valid,
  input               io_axi_r_ready,
  output     [31:0]   io_axi_r_payload_data,
  output     [3:0]    io_axi_r_payload_id,
  output     [1:0]    io_axi_r_payload_resp,
  output              io_axi_r_payload_last,
  output     [19:0]   io_apb_PADDR,
  output reg [0:0]    io_apb_PSEL,
  output reg          io_apb_PENABLE,
  input               io_apb_PREADY,
  output              io_apb_PWRITE,
  output     [31:0]   io_apb_PWDATA,
  input      [31:0]   io_apb_PRDATA,
  input               io_apb_PSLVERROR,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_1_;
  reg        `Axi4ToApb3BridgePhase_defaultEncoding_type phase;
  reg                 write;
  reg        [31:0]   readedData;
  reg        [3:0]    id;
  `ifndef SYNTHESIS
  reg [63:0] phase_string;
  `endif


  assign _zz_1_ = (io_axi_arw_valid && ((! io_axi_arw_payload_write) || io_axi_w_valid));
  `ifndef SYNTHESIS
  always @(*) begin
    case(phase)
      `Axi4ToApb3BridgePhase_defaultEncoding_SETUP : phase_string = "SETUP   ";
      `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 : phase_string = "ACCESS_1";
      `Axi4ToApb3BridgePhase_defaultEncoding_RESPONSE : phase_string = "RESPONSE";
      default : phase_string = "????????";
    endcase
  end
  `endif

  always @ (*) begin
    io_axi_arw_ready = 1'b0;
    case(phase)
      `Axi4ToApb3BridgePhase_defaultEncoding_SETUP : begin
      end
      `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 : begin
        if(io_apb_PREADY)begin
          io_axi_arw_ready = 1'b1;
        end
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    io_axi_w_ready = 1'b0;
    case(phase)
      `Axi4ToApb3BridgePhase_defaultEncoding_SETUP : begin
      end
      `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 : begin
        if(io_apb_PREADY)begin
          io_axi_w_ready = write;
        end
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    io_axi_b_valid = 1'b0;
    case(phase)
      `Axi4ToApb3BridgePhase_defaultEncoding_SETUP : begin
      end
      `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 : begin
      end
      default : begin
        if(write)begin
          io_axi_b_valid = 1'b1;
        end
      end
    endcase
  end

  always @ (*) begin
    io_axi_r_valid = 1'b0;
    case(phase)
      `Axi4ToApb3BridgePhase_defaultEncoding_SETUP : begin
      end
      `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 : begin
      end
      default : begin
        if(! write) begin
          io_axi_r_valid = 1'b1;
        end
      end
    endcase
  end

  always @ (*) begin
    io_apb_PSEL[0] = 1'b0;
    case(phase)
      `Axi4ToApb3BridgePhase_defaultEncoding_SETUP : begin
        if(_zz_1_)begin
          io_apb_PSEL[0] = 1'b1;
        end
      end
      `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 : begin
        io_apb_PSEL[0] = 1'b1;
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    io_apb_PENABLE = 1'b0;
    case(phase)
      `Axi4ToApb3BridgePhase_defaultEncoding_SETUP : begin
      end
      `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 : begin
        io_apb_PENABLE = 1'b1;
      end
      default : begin
      end
    endcase
  end

  assign io_apb_PADDR = io_axi_arw_payload_addr;
  assign io_apb_PWDATA = io_axi_w_payload_data;
  assign io_apb_PWRITE = io_axi_arw_payload_write;
  assign io_axi_r_payload_resp = {io_apb_PSLVERROR,(1'b0)};
  assign io_axi_b_payload_resp = {io_apb_PSLVERROR,(1'b0)};
  assign io_axi_r_payload_id = id;
  assign io_axi_b_payload_id = id;
  assign io_axi_r_payload_data = readedData;
  assign io_axi_r_payload_last = 1'b1;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      phase <= `Axi4ToApb3BridgePhase_defaultEncoding_SETUP;
    end else begin
      case(phase)
        `Axi4ToApb3BridgePhase_defaultEncoding_SETUP : begin
          if(_zz_1_)begin
            phase <= `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1;
          end
        end
        `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 : begin
          if(io_apb_PREADY)begin
            phase <= `Axi4ToApb3BridgePhase_defaultEncoding_RESPONSE;
          end
        end
        default : begin
          if(write)begin
            if(io_axi_b_ready)begin
              phase <= `Axi4ToApb3BridgePhase_defaultEncoding_SETUP;
            end
          end else begin
            if(io_axi_r_ready)begin
              phase <= `Axi4ToApb3BridgePhase_defaultEncoding_SETUP;
            end
          end
        end
      endcase
    end
  end

  always @ (posedge io_clock) begin
    case(phase)
      `Axi4ToApb3BridgePhase_defaultEncoding_SETUP : begin
        write <= io_axi_arw_payload_write;
        id <= io_axi_arw_payload_id;
      end
      `Axi4ToApb3BridgePhase_defaultEncoding_ACCESS_1 : begin
        if(io_apb_PREADY)begin
          readedData <= io_apb_PRDATA;
        end
      end
      default : begin
      end
    endcase
  end


endmodule

module Axi4ReadOnlyDecoder (
  input               io_input_ar_valid,
  output              io_input_ar_ready,
  input      [31:0]   io_input_ar_payload_addr,
  input      [3:0]    io_input_ar_payload_cache,
  input      [2:0]    io_input_ar_payload_prot,
  output reg          io_input_r_valid,
  input               io_input_r_ready,
  output     [31:0]   io_input_r_payload_data,
  output reg [1:0]    io_input_r_payload_resp,
  output reg          io_input_r_payload_last,
  output              io_outputs_0_ar_valid,
  input               io_outputs_0_ar_ready,
  output     [31:0]   io_outputs_0_ar_payload_addr,
  output     [3:0]    io_outputs_0_ar_payload_cache,
  output     [2:0]    io_outputs_0_ar_payload_prot,
  input               io_outputs_0_r_valid,
  output              io_outputs_0_r_ready,
  input      [31:0]   io_outputs_0_r_payload_data,
  input      [1:0]    io_outputs_0_r_payload_resp,
  input               io_outputs_0_r_payload_last,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_1_;
  wire                errorSlave_io_axi_ar_ready;
  wire                errorSlave_io_axi_r_valid;
  wire       [31:0]   errorSlave_io_axi_r_payload_data;
  wire       [1:0]    errorSlave_io_axi_r_payload_resp;
  wire                errorSlave_io_axi_r_payload_last;
  reg                 pendingCmdCounter_incrementIt;
  reg                 pendingCmdCounter_decrementIt;
  wire       [2:0]    pendingCmdCounter_valueNext;
  reg        [2:0]    pendingCmdCounter_value;
  wire                pendingCmdCounter_willOverflowIfInc;
  wire                pendingCmdCounter_willOverflow;
  reg        [2:0]    pendingCmdCounter_finalIncrement;
  wire       [0:0]    decodedCmdSels;
  wire                decodedCmdError;
  reg        [0:0]    pendingSels;
  reg                 pendingError;
  wire                allowCmd;

  Axi4ReadOnlyErrorSlave errorSlave ( 
    .io_axi_ar_valid            (_zz_1_                                  ), //i
    .io_axi_ar_ready            (errorSlave_io_axi_ar_ready              ), //o
    .io_axi_ar_payload_addr     (io_input_ar_payload_addr[31:0]          ), //i
    .io_axi_ar_payload_cache    (io_input_ar_payload_cache[3:0]          ), //i
    .io_axi_ar_payload_prot     (io_input_ar_payload_prot[2:0]           ), //i
    .io_axi_r_valid             (errorSlave_io_axi_r_valid               ), //o
    .io_axi_r_ready             (io_input_r_ready                        ), //i
    .io_axi_r_payload_data      (errorSlave_io_axi_r_payload_data[31:0]  ), //o
    .io_axi_r_payload_resp      (errorSlave_io_axi_r_payload_resp[1:0]   ), //o
    .io_axi_r_payload_last      (errorSlave_io_axi_r_payload_last        ), //o
    .io_clock                   (io_clock                                ), //i
    .resetCtrl_systemReset      (resetCtrl_systemReset                   )  //i
  );
  always @ (*) begin
    pendingCmdCounter_incrementIt = 1'b0;
    if((io_input_ar_valid && io_input_ar_ready))begin
      pendingCmdCounter_incrementIt = 1'b1;
    end
  end

  always @ (*) begin
    pendingCmdCounter_decrementIt = 1'b0;
    if(((io_input_r_valid && io_input_r_ready) && io_input_r_payload_last))begin
      pendingCmdCounter_decrementIt = 1'b1;
    end
  end

  assign pendingCmdCounter_willOverflowIfInc = ((pendingCmdCounter_value == (3'b111)) && (! pendingCmdCounter_decrementIt));
  assign pendingCmdCounter_willOverflow = (pendingCmdCounter_willOverflowIfInc && pendingCmdCounter_incrementIt);
  always @ (*) begin
    if((pendingCmdCounter_incrementIt && (! pendingCmdCounter_decrementIt)))begin
      pendingCmdCounter_finalIncrement = (3'b001);
    end else begin
      if(((! pendingCmdCounter_incrementIt) && pendingCmdCounter_decrementIt))begin
        pendingCmdCounter_finalIncrement = (3'b111);
      end else begin
        pendingCmdCounter_finalIncrement = (3'b000);
      end
    end
  end

  assign pendingCmdCounter_valueNext = (pendingCmdCounter_value + pendingCmdCounter_finalIncrement);
  assign decodedCmdSels = (((io_input_ar_payload_addr & (~ 32'h0001ffff)) == 32'h80000000) && io_input_ar_valid);
  assign decodedCmdError = (decodedCmdSels == (1'b0));
  assign allowCmd = ((pendingCmdCounter_value == (3'b000)) || ((pendingCmdCounter_value != (3'b111)) && (pendingSels == decodedCmdSels)));
  assign io_input_ar_ready = ((((decodedCmdSels & io_outputs_0_ar_ready) != (1'b0)) || (decodedCmdError && errorSlave_io_axi_ar_ready)) && allowCmd);
  assign _zz_1_ = ((io_input_ar_valid && decodedCmdError) && allowCmd);
  assign io_outputs_0_ar_valid = ((io_input_ar_valid && decodedCmdSels[0]) && allowCmd);
  assign io_outputs_0_ar_payload_addr = io_input_ar_payload_addr;
  assign io_outputs_0_ar_payload_cache = io_input_ar_payload_cache;
  assign io_outputs_0_ar_payload_prot = io_input_ar_payload_prot;
  always @ (*) begin
    io_input_r_valid = (io_outputs_0_r_valid != (1'b0));
    if(errorSlave_io_axi_r_valid)begin
      io_input_r_valid = 1'b1;
    end
  end

  assign io_input_r_payload_data = io_outputs_0_r_payload_data;
  always @ (*) begin
    io_input_r_payload_resp = io_outputs_0_r_payload_resp;
    if(pendingError)begin
      io_input_r_payload_resp = errorSlave_io_axi_r_payload_resp;
    end
  end

  always @ (*) begin
    io_input_r_payload_last = io_outputs_0_r_payload_last;
    if(pendingError)begin
      io_input_r_payload_last = errorSlave_io_axi_r_payload_last;
    end
  end

  assign io_outputs_0_r_ready = io_input_r_ready;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      pendingCmdCounter_value <= (3'b000);
      pendingSels <= (1'b0);
      pendingError <= 1'b0;
    end else begin
      pendingCmdCounter_value <= pendingCmdCounter_valueNext;
      if(io_input_ar_ready)begin
        pendingSels <= decodedCmdSels;
      end
      if(io_input_ar_ready)begin
        pendingError <= decodedCmdError;
      end
    end
  end


endmodule

module Axi4SharedDecoder (
  input               io_input_arw_valid,
  output              io_input_arw_ready,
  input      [31:0]   io_input_arw_payload_addr,
  input      [2:0]    io_input_arw_payload_size,
  input      [3:0]    io_input_arw_payload_cache,
  input      [2:0]    io_input_arw_payload_prot,
  input               io_input_arw_payload_write,
  input               io_input_w_valid,
  output              io_input_w_ready,
  input      [31:0]   io_input_w_payload_data,
  input      [3:0]    io_input_w_payload_strb,
  input               io_input_w_payload_last,
  output              io_input_b_valid,
  input               io_input_b_ready,
  output reg [1:0]    io_input_b_payload_resp,
  output              io_input_r_valid,
  input               io_input_r_ready,
  output     [31:0]   io_input_r_payload_data,
  output reg [1:0]    io_input_r_payload_resp,
  output reg          io_input_r_payload_last,
  output              io_sharedOutputs_0_arw_valid,
  input               io_sharedOutputs_0_arw_ready,
  output     [31:0]   io_sharedOutputs_0_arw_payload_addr,
  output     [2:0]    io_sharedOutputs_0_arw_payload_size,
  output     [3:0]    io_sharedOutputs_0_arw_payload_cache,
  output     [2:0]    io_sharedOutputs_0_arw_payload_prot,
  output              io_sharedOutputs_0_arw_payload_write,
  output              io_sharedOutputs_0_w_valid,
  input               io_sharedOutputs_0_w_ready,
  output     [31:0]   io_sharedOutputs_0_w_payload_data,
  output     [3:0]    io_sharedOutputs_0_w_payload_strb,
  output              io_sharedOutputs_0_w_payload_last,
  input               io_sharedOutputs_0_b_valid,
  output              io_sharedOutputs_0_b_ready,
  input      [1:0]    io_sharedOutputs_0_b_payload_resp,
  input               io_sharedOutputs_0_r_valid,
  output              io_sharedOutputs_0_r_ready,
  input      [31:0]   io_sharedOutputs_0_r_payload_data,
  input      [1:0]    io_sharedOutputs_0_r_payload_resp,
  input               io_sharedOutputs_0_r_payload_last,
  output              io_sharedOutputs_1_arw_valid,
  input               io_sharedOutputs_1_arw_ready,
  output     [31:0]   io_sharedOutputs_1_arw_payload_addr,
  output     [2:0]    io_sharedOutputs_1_arw_payload_size,
  output     [3:0]    io_sharedOutputs_1_arw_payload_cache,
  output     [2:0]    io_sharedOutputs_1_arw_payload_prot,
  output              io_sharedOutputs_1_arw_payload_write,
  output              io_sharedOutputs_1_w_valid,
  input               io_sharedOutputs_1_w_ready,
  output     [31:0]   io_sharedOutputs_1_w_payload_data,
  output     [3:0]    io_sharedOutputs_1_w_payload_strb,
  output              io_sharedOutputs_1_w_payload_last,
  input               io_sharedOutputs_1_b_valid,
  output              io_sharedOutputs_1_b_ready,
  input      [1:0]    io_sharedOutputs_1_b_payload_resp,
  input               io_sharedOutputs_1_r_valid,
  output              io_sharedOutputs_1_r_ready,
  input      [31:0]   io_sharedOutputs_1_r_payload_data,
  input      [1:0]    io_sharedOutputs_1_r_payload_resp,
  input               io_sharedOutputs_1_r_payload_last,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_8_;
  wire                _zz_9_;
  reg        [1:0]    _zz_10_;
  reg        [31:0]   _zz_11_;
  reg        [1:0]    _zz_12_;
  reg                 _zz_13_;
  wire                errorSlave_io_axi_arw_ready;
  wire                errorSlave_io_axi_w_ready;
  wire                errorSlave_io_axi_b_valid;
  wire       [1:0]    errorSlave_io_axi_b_payload_resp;
  wire                errorSlave_io_axi_r_valid;
  wire       [31:0]   errorSlave_io_axi_r_payload_data;
  wire       [1:0]    errorSlave_io_axi_r_payload_resp;
  wire                errorSlave_io_axi_r_payload_last;
  wire       [1:0]    _zz_14_;
  wire       [1:0]    _zz_15_;
  reg        [2:0]    _zz_1_;
  reg        [2:0]    _zz_1__1;
  reg        [2:0]    _zz_1__0;
  wire                cmdAllowedStart;
  reg        [2:0]    pendingCmdCounter;
  wire       [2:0]    _zz_2_;
  reg                 pendingDataCounter_incrementIt;
  reg                 pendingDataCounter_decrementIt;
  wire       [2:0]    pendingDataCounter_valueNext;
  reg        [2:0]    pendingDataCounter_value;
  wire                pendingDataCounter_willOverflowIfInc;
  wire                pendingDataCounter_willOverflow;
  reg        [2:0]    pendingDataCounter_finalIncrement;
  wire       [1:0]    decodedCmdSels;
  wire                decodedCmdError;
  reg        [1:0]    pendingSels;
  reg                 pendingError;
  wire                allowCmd;
  wire                allowData;
  reg                 _zz_3_;
  wire       [1:0]    _zz_4_;
  wire       [1:0]    _zz_5_;
  wire                _zz_6_;
  wire       [0:0]    writeRspIndex;
  wire                _zz_7_;
  wire       [0:0]    readRspIndex;

  assign _zz_14_ = pendingSels[1 : 0];
  assign _zz_15_ = pendingSels[1 : 0];
  Axi4SharedErrorSlave errorSlave ( 
    .io_axi_arw_valid            (_zz_8_                                  ), //i
    .io_axi_arw_ready            (errorSlave_io_axi_arw_ready             ), //o
    .io_axi_arw_payload_addr     (io_input_arw_payload_addr[31:0]         ), //i
    .io_axi_arw_payload_size     (io_input_arw_payload_size[2:0]          ), //i
    .io_axi_arw_payload_cache    (io_input_arw_payload_cache[3:0]         ), //i
    .io_axi_arw_payload_prot     (io_input_arw_payload_prot[2:0]          ), //i
    .io_axi_arw_payload_write    (io_input_arw_payload_write              ), //i
    .io_axi_w_valid              (_zz_9_                                  ), //i
    .io_axi_w_ready              (errorSlave_io_axi_w_ready               ), //o
    .io_axi_w_payload_data       (io_input_w_payload_data[31:0]           ), //i
    .io_axi_w_payload_strb       (io_input_w_payload_strb[3:0]            ), //i
    .io_axi_w_payload_last       (io_input_w_payload_last                 ), //i
    .io_axi_b_valid              (errorSlave_io_axi_b_valid               ), //o
    .io_axi_b_ready              (io_input_b_ready                        ), //i
    .io_axi_b_payload_resp       (errorSlave_io_axi_b_payload_resp[1:0]   ), //o
    .io_axi_r_valid              (errorSlave_io_axi_r_valid               ), //o
    .io_axi_r_ready              (io_input_r_ready                        ), //i
    .io_axi_r_payload_data       (errorSlave_io_axi_r_payload_data[31:0]  ), //o
    .io_axi_r_payload_resp       (errorSlave_io_axi_r_payload_resp[1:0]   ), //o
    .io_axi_r_payload_last       (errorSlave_io_axi_r_payload_last        ), //o
    .io_clock                    (io_clock                                ), //i
    .resetCtrl_systemReset       (resetCtrl_systemReset                   )  //i
  );
  always @(*) begin
    case(writeRspIndex)
      1'b0 : begin
        _zz_10_ = io_sharedOutputs_0_b_payload_resp;
      end
      default : begin
        _zz_10_ = io_sharedOutputs_1_b_payload_resp;
      end
    endcase
  end

  always @(*) begin
    case(readRspIndex)
      1'b0 : begin
        _zz_11_ = io_sharedOutputs_0_r_payload_data;
        _zz_12_ = io_sharedOutputs_0_r_payload_resp;
        _zz_13_ = io_sharedOutputs_0_r_payload_last;
      end
      default : begin
        _zz_11_ = io_sharedOutputs_1_r_payload_data;
        _zz_12_ = io_sharedOutputs_1_r_payload_resp;
        _zz_13_ = io_sharedOutputs_1_r_payload_last;
      end
    endcase
  end

  always @ (*) begin
    _zz_1_ = _zz_1__1;
    if(((io_input_r_valid && io_input_r_ready) && io_input_r_payload_last))begin
      _zz_1_ = (_zz_1__1 - (3'b001));
    end
  end

  always @ (*) begin
    _zz_1__1 = _zz_1__0;
    if((io_input_b_valid && io_input_b_ready))begin
      _zz_1__1 = (_zz_1__0 - (3'b001));
    end
  end

  always @ (*) begin
    _zz_1__0 = _zz_2_;
    if((io_input_arw_valid && io_input_arw_ready))begin
      _zz_1__0 = (_zz_2_ + (3'b001));
    end
  end

  assign _zz_2_ = pendingCmdCounter;
  always @ (*) begin
    pendingDataCounter_incrementIt = 1'b0;
    if((cmdAllowedStart && io_input_arw_payload_write))begin
      pendingDataCounter_incrementIt = 1'b1;
    end
  end

  always @ (*) begin
    pendingDataCounter_decrementIt = 1'b0;
    if(((io_input_w_valid && io_input_w_ready) && io_input_w_payload_last))begin
      pendingDataCounter_decrementIt = 1'b1;
    end
  end

  assign pendingDataCounter_willOverflowIfInc = ((pendingDataCounter_value == (3'b111)) && (! pendingDataCounter_decrementIt));
  assign pendingDataCounter_willOverflow = (pendingDataCounter_willOverflowIfInc && pendingDataCounter_incrementIt);
  always @ (*) begin
    if((pendingDataCounter_incrementIt && (! pendingDataCounter_decrementIt)))begin
      pendingDataCounter_finalIncrement = (3'b001);
    end else begin
      if(((! pendingDataCounter_incrementIt) && pendingDataCounter_decrementIt))begin
        pendingDataCounter_finalIncrement = (3'b111);
      end else begin
        pendingDataCounter_finalIncrement = (3'b000);
      end
    end
  end

  assign pendingDataCounter_valueNext = (pendingDataCounter_value + pendingDataCounter_finalIncrement);
  assign decodedCmdSels = {((io_input_arw_payload_addr & (~ 32'h000fffff)) == 32'hf0000000),((io_input_arw_payload_addr & (~ 32'h0001ffff)) == 32'h80000000)};
  assign decodedCmdError = (decodedCmdSels == (2'b00));
  assign allowCmd = ((pendingCmdCounter == (3'b000)) || ((pendingCmdCounter != (3'b111)) && (pendingSels == decodedCmdSels)));
  assign allowData = (pendingDataCounter_value != (3'b000));
  assign cmdAllowedStart = ((io_input_arw_valid && allowCmd) && _zz_3_);
  assign io_input_arw_ready = ((((decodedCmdSels & {io_sharedOutputs_1_arw_ready,io_sharedOutputs_0_arw_ready}) != (2'b00)) || (decodedCmdError && errorSlave_io_axi_arw_ready)) && allowCmd);
  assign _zz_8_ = ((io_input_arw_valid && decodedCmdError) && allowCmd);
  assign _zz_4_ = decodedCmdSels[1 : 0];
  assign io_sharedOutputs_0_arw_valid = ((io_input_arw_valid && _zz_4_[0]) && allowCmd);
  assign io_sharedOutputs_0_arw_payload_addr = io_input_arw_payload_addr;
  assign io_sharedOutputs_0_arw_payload_size = io_input_arw_payload_size;
  assign io_sharedOutputs_0_arw_payload_cache = io_input_arw_payload_cache;
  assign io_sharedOutputs_0_arw_payload_prot = io_input_arw_payload_prot;
  assign io_sharedOutputs_0_arw_payload_write = io_input_arw_payload_write;
  assign io_sharedOutputs_1_arw_valid = ((io_input_arw_valid && _zz_4_[1]) && allowCmd);
  assign io_sharedOutputs_1_arw_payload_addr = io_input_arw_payload_addr;
  assign io_sharedOutputs_1_arw_payload_size = io_input_arw_payload_size;
  assign io_sharedOutputs_1_arw_payload_cache = io_input_arw_payload_cache;
  assign io_sharedOutputs_1_arw_payload_prot = io_input_arw_payload_prot;
  assign io_sharedOutputs_1_arw_payload_write = io_input_arw_payload_write;
  assign io_input_w_ready = ((((pendingSels[1 : 0] & {io_sharedOutputs_1_w_ready,io_sharedOutputs_0_w_ready}) != (2'b00)) || (pendingError && errorSlave_io_axi_w_ready)) && allowData);
  assign _zz_9_ = ((io_input_w_valid && pendingError) && allowData);
  assign _zz_5_ = pendingSels[1 : 0];
  assign io_sharedOutputs_0_w_valid = ((io_input_w_valid && _zz_5_[0]) && allowData);
  assign io_sharedOutputs_0_w_payload_data = io_input_w_payload_data;
  assign io_sharedOutputs_0_w_payload_strb = io_input_w_payload_strb;
  assign io_sharedOutputs_0_w_payload_last = io_input_w_payload_last;
  assign io_sharedOutputs_1_w_valid = ((io_input_w_valid && _zz_5_[1]) && allowData);
  assign io_sharedOutputs_1_w_payload_data = io_input_w_payload_data;
  assign io_sharedOutputs_1_w_payload_strb = io_input_w_payload_strb;
  assign io_sharedOutputs_1_w_payload_last = io_input_w_payload_last;
  assign _zz_6_ = _zz_14_[1];
  assign writeRspIndex = _zz_6_;
  assign io_input_b_valid = (({io_sharedOutputs_1_b_valid,io_sharedOutputs_0_b_valid} != (2'b00)) || errorSlave_io_axi_b_valid);
  always @ (*) begin
    io_input_b_payload_resp = _zz_10_;
    if(pendingError)begin
      io_input_b_payload_resp = errorSlave_io_axi_b_payload_resp;
    end
  end

  assign io_sharedOutputs_0_b_ready = io_input_b_ready;
  assign io_sharedOutputs_1_b_ready = io_input_b_ready;
  assign _zz_7_ = _zz_15_[1];
  assign readRspIndex = _zz_7_;
  assign io_input_r_valid = (({io_sharedOutputs_1_r_valid,io_sharedOutputs_0_r_valid} != (2'b00)) || errorSlave_io_axi_r_valid);
  assign io_input_r_payload_data = _zz_11_;
  always @ (*) begin
    io_input_r_payload_resp = _zz_12_;
    if(pendingError)begin
      io_input_r_payload_resp = errorSlave_io_axi_r_payload_resp;
    end
  end

  always @ (*) begin
    io_input_r_payload_last = _zz_13_;
    if(pendingError)begin
      io_input_r_payload_last = errorSlave_io_axi_r_payload_last;
    end
  end

  assign io_sharedOutputs_0_r_ready = io_input_r_ready;
  assign io_sharedOutputs_1_r_ready = io_input_r_ready;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      pendingCmdCounter <= (3'b000);
      pendingDataCounter_value <= (3'b000);
      pendingSels <= (2'b00);
      pendingError <= 1'b0;
      _zz_3_ <= 1'b1;
    end else begin
      pendingCmdCounter <= _zz_1_;
      pendingDataCounter_value <= pendingDataCounter_valueNext;
      if(cmdAllowedStart)begin
        pendingSels <= decodedCmdSels;
      end
      if(cmdAllowedStart)begin
        pendingError <= decodedCmdError;
      end
      if(cmdAllowedStart)begin
        _zz_3_ <= 1'b0;
      end
      if(io_input_arw_ready)begin
        _zz_3_ <= 1'b1;
      end
    end
  end


endmodule

module Axi4SharedArbiter (
  input               io_readInputs_0_ar_valid,
  output              io_readInputs_0_ar_ready,
  input      [16:0]   io_readInputs_0_ar_payload_addr,
  input      [2:0]    io_readInputs_0_ar_payload_id,
  input      [7:0]    io_readInputs_0_ar_payload_len,
  input      [2:0]    io_readInputs_0_ar_payload_size,
  input      [1:0]    io_readInputs_0_ar_payload_burst,
  output              io_readInputs_0_r_valid,
  input               io_readInputs_0_r_ready,
  output     [31:0]   io_readInputs_0_r_payload_data,
  output     [2:0]    io_readInputs_0_r_payload_id,
  output     [1:0]    io_readInputs_0_r_payload_resp,
  output              io_readInputs_0_r_payload_last,
  input               io_sharedInputs_0_arw_valid,
  output              io_sharedInputs_0_arw_ready,
  input      [16:0]   io_sharedInputs_0_arw_payload_addr,
  input      [2:0]    io_sharedInputs_0_arw_payload_id,
  input      [7:0]    io_sharedInputs_0_arw_payload_len,
  input      [2:0]    io_sharedInputs_0_arw_payload_size,
  input      [1:0]    io_sharedInputs_0_arw_payload_burst,
  input               io_sharedInputs_0_arw_payload_write,
  input               io_sharedInputs_0_w_valid,
  output              io_sharedInputs_0_w_ready,
  input      [31:0]   io_sharedInputs_0_w_payload_data,
  input      [3:0]    io_sharedInputs_0_w_payload_strb,
  input               io_sharedInputs_0_w_payload_last,
  output              io_sharedInputs_0_b_valid,
  input               io_sharedInputs_0_b_ready,
  output     [2:0]    io_sharedInputs_0_b_payload_id,
  output     [1:0]    io_sharedInputs_0_b_payload_resp,
  output              io_sharedInputs_0_r_valid,
  input               io_sharedInputs_0_r_ready,
  output     [31:0]   io_sharedInputs_0_r_payload_data,
  output     [2:0]    io_sharedInputs_0_r_payload_id,
  output     [1:0]    io_sharedInputs_0_r_payload_resp,
  output              io_sharedInputs_0_r_payload_last,
  output              io_output_arw_valid,
  input               io_output_arw_ready,
  output     [16:0]   io_output_arw_payload_addr,
  output     [3:0]    io_output_arw_payload_id,
  output     [7:0]    io_output_arw_payload_len,
  output     [2:0]    io_output_arw_payload_size,
  output     [1:0]    io_output_arw_payload_burst,
  output              io_output_arw_payload_write,
  output              io_output_w_valid,
  input               io_output_w_ready,
  output     [31:0]   io_output_w_payload_data,
  output     [3:0]    io_output_w_payload_strb,
  output              io_output_w_payload_last,
  input               io_output_b_valid,
  output              io_output_b_ready,
  input      [3:0]    io_output_b_payload_id,
  input      [1:0]    io_output_b_payload_resp,
  input               io_output_r_valid,
  output              io_output_r_ready,
  input      [31:0]   io_output_r_payload_data,
  input      [3:0]    io_output_r_payload_id,
  input      [1:0]    io_output_r_payload_resp,
  input               io_output_r_payload_last,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg                 _zz_2_;
  wire                _zz_3_;
  wire                _zz_4_;
  reg                 _zz_5_;
  wire                cmdArbiter_io_inputs_0_ready;
  wire                cmdArbiter_io_inputs_1_ready;
  wire                cmdArbiter_io_output_valid;
  wire       [16:0]   cmdArbiter_io_output_payload_addr;
  wire       [2:0]    cmdArbiter_io_output_payload_id;
  wire       [7:0]    cmdArbiter_io_output_payload_len;
  wire       [2:0]    cmdArbiter_io_output_payload_size;
  wire       [1:0]    cmdArbiter_io_output_payload_burst;
  wire                cmdArbiter_io_output_payload_write;
  wire       [0:0]    cmdArbiter_io_chosen;
  wire       [1:0]    cmdArbiter_io_chosenOH;
  wire                streamFork_3__io_input_ready;
  wire                streamFork_3__io_outputs_0_valid;
  wire       [16:0]   streamFork_3__io_outputs_0_payload_addr;
  wire       [2:0]    streamFork_3__io_outputs_0_payload_id;
  wire       [7:0]    streamFork_3__io_outputs_0_payload_len;
  wire       [2:0]    streamFork_3__io_outputs_0_payload_size;
  wire       [1:0]    streamFork_3__io_outputs_0_payload_burst;
  wire                streamFork_3__io_outputs_0_payload_write;
  wire                streamFork_3__io_outputs_1_valid;
  wire       [16:0]   streamFork_3__io_outputs_1_payload_addr;
  wire       [2:0]    streamFork_3__io_outputs_1_payload_id;
  wire       [7:0]    streamFork_3__io_outputs_1_payload_len;
  wire       [2:0]    streamFork_3__io_outputs_1_payload_size;
  wire       [1:0]    streamFork_3__io_outputs_1_payload_burst;
  wire                streamFork_3__io_outputs_1_payload_write;
  wire                streamFork_3__io_outputs_1_thrown_translated_fifo_io_push_ready;
  wire                streamFork_3__io_outputs_1_thrown_translated_fifo_io_pop_valid;
  wire       [2:0]    streamFork_3__io_outputs_1_thrown_translated_fifo_io_occupancy;
  wire                _zz_6_;
  wire       [1:0]    _zz_7_;
  wire       [2:0]    _zz_8_;
  wire       [3:0]    _zz_9_;
  wire                inputsCmd_0_valid;
  wire                inputsCmd_0_ready;
  wire       [16:0]   inputsCmd_0_payload_addr;
  wire       [2:0]    inputsCmd_0_payload_id;
  wire       [7:0]    inputsCmd_0_payload_len;
  wire       [2:0]    inputsCmd_0_payload_size;
  wire       [1:0]    inputsCmd_0_payload_burst;
  wire                inputsCmd_0_payload_write;
  wire                inputsCmd_1_valid;
  wire                inputsCmd_1_ready;
  wire       [16:0]   inputsCmd_1_payload_addr;
  wire       [2:0]    inputsCmd_1_payload_id;
  wire       [7:0]    inputsCmd_1_payload_len;
  wire       [2:0]    inputsCmd_1_payload_size;
  wire       [1:0]    inputsCmd_1_payload_burst;
  wire                inputsCmd_1_payload_write;
  wire                _zz_1_;
  reg                 streamFork_3__io_outputs_1_thrown_valid;
  wire                streamFork_3__io_outputs_1_thrown_ready;
  wire       [16:0]   streamFork_3__io_outputs_1_thrown_payload_addr;
  wire       [2:0]    streamFork_3__io_outputs_1_thrown_payload_id;
  wire       [7:0]    streamFork_3__io_outputs_1_thrown_payload_len;
  wire       [2:0]    streamFork_3__io_outputs_1_thrown_payload_size;
  wire       [1:0]    streamFork_3__io_outputs_1_thrown_payload_burst;
  wire                streamFork_3__io_outputs_1_thrown_payload_write;
  wire                streamFork_3__io_outputs_1_thrown_translated_valid;
  wire                streamFork_3__io_outputs_1_thrown_translated_ready;
  wire                writeLogic_routeDataInput_valid;
  wire                writeLogic_routeDataInput_ready;
  wire       [31:0]   writeLogic_routeDataInput_payload_data;
  wire       [3:0]    writeLogic_routeDataInput_payload_strb;
  wire                writeLogic_routeDataInput_payload_last;
  wire                writeLogic_writeRspSels_0;
  wire       [0:0]    readRspIndex;
  wire                readRspSels_0;
  wire                readRspSels_1;

  assign _zz_6_ = (! streamFork_3__io_outputs_1_payload_write);
  assign _zz_7_ = {cmdArbiter_io_chosenOH[1 : 1],cmdArbiter_io_chosenOH[0 : 0]};
  assign _zz_8_ = streamFork_3__io_outputs_0_payload_id;
  assign _zz_9_ = {1'd0, _zz_8_};
  StreamArbiter cmdArbiter ( 
    .io_inputs_0_valid            (inputsCmd_0_valid                        ), //i
    .io_inputs_0_ready            (cmdArbiter_io_inputs_0_ready             ), //o
    .io_inputs_0_payload_addr     (inputsCmd_0_payload_addr[16:0]           ), //i
    .io_inputs_0_payload_id       (inputsCmd_0_payload_id[2:0]              ), //i
    .io_inputs_0_payload_len      (inputsCmd_0_payload_len[7:0]             ), //i
    .io_inputs_0_payload_size     (inputsCmd_0_payload_size[2:0]            ), //i
    .io_inputs_0_payload_burst    (inputsCmd_0_payload_burst[1:0]           ), //i
    .io_inputs_0_payload_write    (inputsCmd_0_payload_write                ), //i
    .io_inputs_1_valid            (inputsCmd_1_valid                        ), //i
    .io_inputs_1_ready            (cmdArbiter_io_inputs_1_ready             ), //o
    .io_inputs_1_payload_addr     (inputsCmd_1_payload_addr[16:0]           ), //i
    .io_inputs_1_payload_id       (inputsCmd_1_payload_id[2:0]              ), //i
    .io_inputs_1_payload_len      (inputsCmd_1_payload_len[7:0]             ), //i
    .io_inputs_1_payload_size     (inputsCmd_1_payload_size[2:0]            ), //i
    .io_inputs_1_payload_burst    (inputsCmd_1_payload_burst[1:0]           ), //i
    .io_inputs_1_payload_write    (inputsCmd_1_payload_write                ), //i
    .io_output_valid              (cmdArbiter_io_output_valid               ), //o
    .io_output_ready              (streamFork_3__io_input_ready             ), //i
    .io_output_payload_addr       (cmdArbiter_io_output_payload_addr[16:0]  ), //o
    .io_output_payload_id         (cmdArbiter_io_output_payload_id[2:0]     ), //o
    .io_output_payload_len        (cmdArbiter_io_output_payload_len[7:0]    ), //o
    .io_output_payload_size       (cmdArbiter_io_output_payload_size[2:0]   ), //o
    .io_output_payload_burst      (cmdArbiter_io_output_payload_burst[1:0]  ), //o
    .io_output_payload_write      (cmdArbiter_io_output_payload_write       ), //o
    .io_chosen                    (cmdArbiter_io_chosen                     ), //o
    .io_chosenOH                  (cmdArbiter_io_chosenOH[1:0]              ), //o
    .io_clock                     (io_clock                                 ), //i
    .resetCtrl_systemReset        (resetCtrl_systemReset                    )  //i
  );
  StreamFork streamFork_3_ ( 
    .io_input_valid                (cmdArbiter_io_output_valid                     ), //i
    .io_input_ready                (streamFork_3__io_input_ready                   ), //o
    .io_input_payload_addr         (cmdArbiter_io_output_payload_addr[16:0]        ), //i
    .io_input_payload_id           (cmdArbiter_io_output_payload_id[2:0]           ), //i
    .io_input_payload_len          (cmdArbiter_io_output_payload_len[7:0]          ), //i
    .io_input_payload_size         (cmdArbiter_io_output_payload_size[2:0]         ), //i
    .io_input_payload_burst        (cmdArbiter_io_output_payload_burst[1:0]        ), //i
    .io_input_payload_write        (cmdArbiter_io_output_payload_write             ), //i
    .io_outputs_0_valid            (streamFork_3__io_outputs_0_valid               ), //o
    .io_outputs_0_ready            (io_output_arw_ready                            ), //i
    .io_outputs_0_payload_addr     (streamFork_3__io_outputs_0_payload_addr[16:0]  ), //o
    .io_outputs_0_payload_id       (streamFork_3__io_outputs_0_payload_id[2:0]     ), //o
    .io_outputs_0_payload_len      (streamFork_3__io_outputs_0_payload_len[7:0]    ), //o
    .io_outputs_0_payload_size     (streamFork_3__io_outputs_0_payload_size[2:0]   ), //o
    .io_outputs_0_payload_burst    (streamFork_3__io_outputs_0_payload_burst[1:0]  ), //o
    .io_outputs_0_payload_write    (streamFork_3__io_outputs_0_payload_write       ), //o
    .io_outputs_1_valid            (streamFork_3__io_outputs_1_valid               ), //o
    .io_outputs_1_ready            (_zz_2_                                         ), //i
    .io_outputs_1_payload_addr     (streamFork_3__io_outputs_1_payload_addr[16:0]  ), //o
    .io_outputs_1_payload_id       (streamFork_3__io_outputs_1_payload_id[2:0]     ), //o
    .io_outputs_1_payload_len      (streamFork_3__io_outputs_1_payload_len[7:0]    ), //o
    .io_outputs_1_payload_size     (streamFork_3__io_outputs_1_payload_size[2:0]   ), //o
    .io_outputs_1_payload_burst    (streamFork_3__io_outputs_1_payload_burst[1:0]  ), //o
    .io_outputs_1_payload_write    (streamFork_3__io_outputs_1_payload_write       ), //o
    .io_clock                      (io_clock                                       ), //i
    .resetCtrl_systemReset         (resetCtrl_systemReset                          )  //i
  );
  StreamFifoLowLatency_1_ streamFork_3__io_outputs_1_thrown_translated_fifo ( 
    .io_push_valid            (streamFork_3__io_outputs_1_thrown_translated_valid                   ), //i
    .io_push_ready            (streamFork_3__io_outputs_1_thrown_translated_fifo_io_push_ready      ), //o
    .io_pop_valid             (streamFork_3__io_outputs_1_thrown_translated_fifo_io_pop_valid       ), //o
    .io_pop_ready             (_zz_3_                                                               ), //i
    .io_flush                 (_zz_4_                                                               ), //i
    .io_occupancy             (streamFork_3__io_outputs_1_thrown_translated_fifo_io_occupancy[2:0]  ), //o
    .io_clock                 (io_clock                                                             ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                                                )  //i
  );
  always @(*) begin
    case(readRspIndex)
      1'b0 : begin
        _zz_5_ = io_readInputs_0_r_ready;
      end
      default : begin
        _zz_5_ = io_sharedInputs_0_r_ready;
      end
    endcase
  end

  assign inputsCmd_0_valid = io_readInputs_0_ar_valid;
  assign io_readInputs_0_ar_ready = inputsCmd_0_ready;
  assign inputsCmd_0_payload_addr = io_readInputs_0_ar_payload_addr;
  assign inputsCmd_0_payload_id = io_readInputs_0_ar_payload_id;
  assign inputsCmd_0_payload_len = io_readInputs_0_ar_payload_len;
  assign inputsCmd_0_payload_size = io_readInputs_0_ar_payload_size;
  assign inputsCmd_0_payload_burst = io_readInputs_0_ar_payload_burst;
  assign inputsCmd_0_payload_write = 1'b0;
  assign inputsCmd_1_valid = io_sharedInputs_0_arw_valid;
  assign io_sharedInputs_0_arw_ready = inputsCmd_1_ready;
  assign inputsCmd_1_payload_addr = io_sharedInputs_0_arw_payload_addr;
  assign inputsCmd_1_payload_id = io_sharedInputs_0_arw_payload_id;
  assign inputsCmd_1_payload_len = io_sharedInputs_0_arw_payload_len;
  assign inputsCmd_1_payload_size = io_sharedInputs_0_arw_payload_size;
  assign inputsCmd_1_payload_burst = io_sharedInputs_0_arw_payload_burst;
  assign inputsCmd_1_payload_write = io_sharedInputs_0_arw_payload_write;
  assign inputsCmd_0_ready = cmdArbiter_io_inputs_0_ready;
  assign inputsCmd_1_ready = cmdArbiter_io_inputs_1_ready;
  assign io_output_arw_valid = streamFork_3__io_outputs_0_valid;
  assign io_output_arw_payload_addr = streamFork_3__io_outputs_0_payload_addr;
  assign io_output_arw_payload_len = streamFork_3__io_outputs_0_payload_len;
  assign io_output_arw_payload_size = streamFork_3__io_outputs_0_payload_size;
  assign io_output_arw_payload_burst = streamFork_3__io_outputs_0_payload_burst;
  assign io_output_arw_payload_write = streamFork_3__io_outputs_0_payload_write;
  assign _zz_1_ = _zz_7_[1];
  assign io_output_arw_payload_id = (streamFork_3__io_outputs_0_payload_write ? _zz_9_ : {_zz_1_,streamFork_3__io_outputs_0_payload_id});
  always @ (*) begin
    streamFork_3__io_outputs_1_thrown_valid = streamFork_3__io_outputs_1_valid;
    if(_zz_6_)begin
      streamFork_3__io_outputs_1_thrown_valid = 1'b0;
    end
  end

  always @ (*) begin
    _zz_2_ = streamFork_3__io_outputs_1_thrown_ready;
    if(_zz_6_)begin
      _zz_2_ = 1'b1;
    end
  end

  assign streamFork_3__io_outputs_1_thrown_payload_addr = streamFork_3__io_outputs_1_payload_addr;
  assign streamFork_3__io_outputs_1_thrown_payload_id = streamFork_3__io_outputs_1_payload_id;
  assign streamFork_3__io_outputs_1_thrown_payload_len = streamFork_3__io_outputs_1_payload_len;
  assign streamFork_3__io_outputs_1_thrown_payload_size = streamFork_3__io_outputs_1_payload_size;
  assign streamFork_3__io_outputs_1_thrown_payload_burst = streamFork_3__io_outputs_1_payload_burst;
  assign streamFork_3__io_outputs_1_thrown_payload_write = streamFork_3__io_outputs_1_payload_write;
  assign streamFork_3__io_outputs_1_thrown_translated_valid = streamFork_3__io_outputs_1_thrown_valid;
  assign streamFork_3__io_outputs_1_thrown_ready = streamFork_3__io_outputs_1_thrown_translated_ready;
  assign streamFork_3__io_outputs_1_thrown_translated_ready = streamFork_3__io_outputs_1_thrown_translated_fifo_io_push_ready;
  assign writeLogic_routeDataInput_valid = io_sharedInputs_0_w_valid;
  assign writeLogic_routeDataInput_ready = io_sharedInputs_0_w_ready;
  assign writeLogic_routeDataInput_payload_data = io_sharedInputs_0_w_payload_data;
  assign writeLogic_routeDataInput_payload_strb = io_sharedInputs_0_w_payload_strb;
  assign writeLogic_routeDataInput_payload_last = io_sharedInputs_0_w_payload_last;
  assign io_output_w_valid = (streamFork_3__io_outputs_1_thrown_translated_fifo_io_pop_valid && writeLogic_routeDataInput_valid);
  assign io_output_w_payload_data = writeLogic_routeDataInput_payload_data;
  assign io_output_w_payload_strb = writeLogic_routeDataInput_payload_strb;
  assign io_output_w_payload_last = writeLogic_routeDataInput_payload_last;
  assign io_sharedInputs_0_w_ready = ((streamFork_3__io_outputs_1_thrown_translated_fifo_io_pop_valid && io_output_w_ready) && 1'b1);
  assign _zz_3_ = ((io_output_w_valid && io_output_w_ready) && io_output_w_payload_last);
  assign writeLogic_writeRspSels_0 = 1'b1;
  assign io_sharedInputs_0_b_valid = (io_output_b_valid && writeLogic_writeRspSels_0);
  assign io_sharedInputs_0_b_payload_resp = io_output_b_payload_resp;
  assign io_sharedInputs_0_b_payload_id = io_output_b_payload_id[2:0];
  assign io_output_b_ready = io_sharedInputs_0_b_ready;
  assign readRspIndex = io_output_r_payload_id[3 : 3];
  assign readRspSels_0 = (readRspIndex == (1'b0));
  assign readRspSels_1 = (readRspIndex == (1'b1));
  assign io_readInputs_0_r_valid = (io_output_r_valid && readRspSels_0);
  assign io_readInputs_0_r_payload_data = io_output_r_payload_data;
  assign io_readInputs_0_r_payload_resp = io_output_r_payload_resp;
  assign io_readInputs_0_r_payload_last = io_output_r_payload_last;
  assign io_readInputs_0_r_payload_id = io_output_r_payload_id[2:0];
  assign io_sharedInputs_0_r_valid = (io_output_r_valid && readRspSels_1);
  assign io_sharedInputs_0_r_payload_data = io_output_r_payload_data;
  assign io_sharedInputs_0_r_payload_resp = io_output_r_payload_resp;
  assign io_sharedInputs_0_r_payload_last = io_output_r_payload_last;
  assign io_sharedInputs_0_r_payload_id = io_output_r_payload_id[2:0];
  assign io_output_r_ready = _zz_5_;
  assign _zz_4_ = 1'b0;

endmodule

module Axi4SharedArbiter_1_ (
  input               io_sharedInputs_0_arw_valid,
  output              io_sharedInputs_0_arw_ready,
  input      [19:0]   io_sharedInputs_0_arw_payload_addr,
  input      [3:0]    io_sharedInputs_0_arw_payload_id,
  input      [7:0]    io_sharedInputs_0_arw_payload_len,
  input      [2:0]    io_sharedInputs_0_arw_payload_size,
  input      [1:0]    io_sharedInputs_0_arw_payload_burst,
  input               io_sharedInputs_0_arw_payload_write,
  input               io_sharedInputs_0_w_valid,
  output              io_sharedInputs_0_w_ready,
  input      [31:0]   io_sharedInputs_0_w_payload_data,
  input      [3:0]    io_sharedInputs_0_w_payload_strb,
  input               io_sharedInputs_0_w_payload_last,
  output              io_sharedInputs_0_b_valid,
  input               io_sharedInputs_0_b_ready,
  output     [3:0]    io_sharedInputs_0_b_payload_id,
  output     [1:0]    io_sharedInputs_0_b_payload_resp,
  output              io_sharedInputs_0_r_valid,
  input               io_sharedInputs_0_r_ready,
  output     [31:0]   io_sharedInputs_0_r_payload_data,
  output     [3:0]    io_sharedInputs_0_r_payload_id,
  output     [1:0]    io_sharedInputs_0_r_payload_resp,
  output              io_sharedInputs_0_r_payload_last,
  output              io_output_arw_valid,
  input               io_output_arw_ready,
  output     [19:0]   io_output_arw_payload_addr,
  output     [3:0]    io_output_arw_payload_id,
  output     [7:0]    io_output_arw_payload_len,
  output     [2:0]    io_output_arw_payload_size,
  output     [1:0]    io_output_arw_payload_burst,
  output              io_output_arw_payload_write,
  output              io_output_w_valid,
  input               io_output_w_ready,
  output     [31:0]   io_output_w_payload_data,
  output     [3:0]    io_output_w_payload_strb,
  output              io_output_w_payload_last,
  input               io_output_b_valid,
  output              io_output_b_ready,
  input      [3:0]    io_output_b_payload_id,
  input      [1:0]    io_output_b_payload_resp,
  input               io_output_r_valid,
  output              io_output_r_ready,
  input      [31:0]   io_output_r_payload_data,
  input      [3:0]    io_output_r_payload_id,
  input      [1:0]    io_output_r_payload_resp,
  input               io_output_r_payload_last,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg                 _zz_1_;
  wire                _zz_2_;
  wire                _zz_3_;
  wire                cmdArbiter_io_inputs_0_ready;
  wire                cmdArbiter_io_output_valid;
  wire       [19:0]   cmdArbiter_io_output_payload_addr;
  wire       [3:0]    cmdArbiter_io_output_payload_id;
  wire       [7:0]    cmdArbiter_io_output_payload_len;
  wire       [2:0]    cmdArbiter_io_output_payload_size;
  wire       [1:0]    cmdArbiter_io_output_payload_burst;
  wire                cmdArbiter_io_output_payload_write;
  wire       [0:0]    cmdArbiter_io_chosenOH;
  wire                streamFork_3__io_input_ready;
  wire                streamFork_3__io_outputs_0_valid;
  wire       [19:0]   streamFork_3__io_outputs_0_payload_addr;
  wire       [3:0]    streamFork_3__io_outputs_0_payload_id;
  wire       [7:0]    streamFork_3__io_outputs_0_payload_len;
  wire       [2:0]    streamFork_3__io_outputs_0_payload_size;
  wire       [1:0]    streamFork_3__io_outputs_0_payload_burst;
  wire                streamFork_3__io_outputs_0_payload_write;
  wire                streamFork_3__io_outputs_1_valid;
  wire       [19:0]   streamFork_3__io_outputs_1_payload_addr;
  wire       [3:0]    streamFork_3__io_outputs_1_payload_id;
  wire       [7:0]    streamFork_3__io_outputs_1_payload_len;
  wire       [2:0]    streamFork_3__io_outputs_1_payload_size;
  wire       [1:0]    streamFork_3__io_outputs_1_payload_burst;
  wire                streamFork_3__io_outputs_1_payload_write;
  wire                streamFork_3__io_outputs_1_thrown_translated_fifo_io_push_ready;
  wire                streamFork_3__io_outputs_1_thrown_translated_fifo_io_pop_valid;
  wire       [2:0]    streamFork_3__io_outputs_1_thrown_translated_fifo_io_occupancy;
  wire                _zz_4_;
  wire                inputsCmd_0_valid;
  wire                inputsCmd_0_ready;
  wire       [19:0]   inputsCmd_0_payload_addr;
  wire       [3:0]    inputsCmd_0_payload_id;
  wire       [7:0]    inputsCmd_0_payload_len;
  wire       [2:0]    inputsCmd_0_payload_size;
  wire       [1:0]    inputsCmd_0_payload_burst;
  wire                inputsCmd_0_payload_write;
  reg                 streamFork_3__io_outputs_1_thrown_valid;
  wire                streamFork_3__io_outputs_1_thrown_ready;
  wire       [19:0]   streamFork_3__io_outputs_1_thrown_payload_addr;
  wire       [3:0]    streamFork_3__io_outputs_1_thrown_payload_id;
  wire       [7:0]    streamFork_3__io_outputs_1_thrown_payload_len;
  wire       [2:0]    streamFork_3__io_outputs_1_thrown_payload_size;
  wire       [1:0]    streamFork_3__io_outputs_1_thrown_payload_burst;
  wire                streamFork_3__io_outputs_1_thrown_payload_write;
  wire                streamFork_3__io_outputs_1_thrown_translated_valid;
  wire                streamFork_3__io_outputs_1_thrown_translated_ready;
  wire                writeLogic_routeDataInput_valid;
  wire                writeLogic_routeDataInput_ready;
  wire       [31:0]   writeLogic_routeDataInput_payload_data;
  wire       [3:0]    writeLogic_routeDataInput_payload_strb;
  wire                writeLogic_routeDataInput_payload_last;
  wire                writeLogic_writeRspSels_0;
  wire                readRspSels_0;

  assign _zz_4_ = (! streamFork_3__io_outputs_1_payload_write);
  StreamArbiter_1_ cmdArbiter ( 
    .io_inputs_0_valid            (inputsCmd_0_valid                        ), //i
    .io_inputs_0_ready            (cmdArbiter_io_inputs_0_ready             ), //o
    .io_inputs_0_payload_addr     (inputsCmd_0_payload_addr[19:0]           ), //i
    .io_inputs_0_payload_id       (inputsCmd_0_payload_id[3:0]              ), //i
    .io_inputs_0_payload_len      (inputsCmd_0_payload_len[7:0]             ), //i
    .io_inputs_0_payload_size     (inputsCmd_0_payload_size[2:0]            ), //i
    .io_inputs_0_payload_burst    (inputsCmd_0_payload_burst[1:0]           ), //i
    .io_inputs_0_payload_write    (inputsCmd_0_payload_write                ), //i
    .io_output_valid              (cmdArbiter_io_output_valid               ), //o
    .io_output_ready              (streamFork_3__io_input_ready             ), //i
    .io_output_payload_addr       (cmdArbiter_io_output_payload_addr[19:0]  ), //o
    .io_output_payload_id         (cmdArbiter_io_output_payload_id[3:0]     ), //o
    .io_output_payload_len        (cmdArbiter_io_output_payload_len[7:0]    ), //o
    .io_output_payload_size       (cmdArbiter_io_output_payload_size[2:0]   ), //o
    .io_output_payload_burst      (cmdArbiter_io_output_payload_burst[1:0]  ), //o
    .io_output_payload_write      (cmdArbiter_io_output_payload_write       ), //o
    .io_chosenOH                  (cmdArbiter_io_chosenOH                   ), //o
    .io_clock                     (io_clock                                 ), //i
    .resetCtrl_systemReset        (resetCtrl_systemReset                    )  //i
  );
  StreamFork_1_ streamFork_3_ ( 
    .io_input_valid                (cmdArbiter_io_output_valid                     ), //i
    .io_input_ready                (streamFork_3__io_input_ready                   ), //o
    .io_input_payload_addr         (cmdArbiter_io_output_payload_addr[19:0]        ), //i
    .io_input_payload_id           (cmdArbiter_io_output_payload_id[3:0]           ), //i
    .io_input_payload_len          (cmdArbiter_io_output_payload_len[7:0]          ), //i
    .io_input_payload_size         (cmdArbiter_io_output_payload_size[2:0]         ), //i
    .io_input_payload_burst        (cmdArbiter_io_output_payload_burst[1:0]        ), //i
    .io_input_payload_write        (cmdArbiter_io_output_payload_write             ), //i
    .io_outputs_0_valid            (streamFork_3__io_outputs_0_valid               ), //o
    .io_outputs_0_ready            (io_output_arw_ready                            ), //i
    .io_outputs_0_payload_addr     (streamFork_3__io_outputs_0_payload_addr[19:0]  ), //o
    .io_outputs_0_payload_id       (streamFork_3__io_outputs_0_payload_id[3:0]     ), //o
    .io_outputs_0_payload_len      (streamFork_3__io_outputs_0_payload_len[7:0]    ), //o
    .io_outputs_0_payload_size     (streamFork_3__io_outputs_0_payload_size[2:0]   ), //o
    .io_outputs_0_payload_burst    (streamFork_3__io_outputs_0_payload_burst[1:0]  ), //o
    .io_outputs_0_payload_write    (streamFork_3__io_outputs_0_payload_write       ), //o
    .io_outputs_1_valid            (streamFork_3__io_outputs_1_valid               ), //o
    .io_outputs_1_ready            (_zz_1_                                         ), //i
    .io_outputs_1_payload_addr     (streamFork_3__io_outputs_1_payload_addr[19:0]  ), //o
    .io_outputs_1_payload_id       (streamFork_3__io_outputs_1_payload_id[3:0]     ), //o
    .io_outputs_1_payload_len      (streamFork_3__io_outputs_1_payload_len[7:0]    ), //o
    .io_outputs_1_payload_size     (streamFork_3__io_outputs_1_payload_size[2:0]   ), //o
    .io_outputs_1_payload_burst    (streamFork_3__io_outputs_1_payload_burst[1:0]  ), //o
    .io_outputs_1_payload_write    (streamFork_3__io_outputs_1_payload_write       ), //o
    .io_clock                      (io_clock                                       ), //i
    .resetCtrl_systemReset         (resetCtrl_systemReset                          )  //i
  );
  StreamFifoLowLatency_1_ streamFork_3__io_outputs_1_thrown_translated_fifo ( 
    .io_push_valid            (streamFork_3__io_outputs_1_thrown_translated_valid                   ), //i
    .io_push_ready            (streamFork_3__io_outputs_1_thrown_translated_fifo_io_push_ready      ), //o
    .io_pop_valid             (streamFork_3__io_outputs_1_thrown_translated_fifo_io_pop_valid       ), //o
    .io_pop_ready             (_zz_2_                                                               ), //i
    .io_flush                 (_zz_3_                                                               ), //i
    .io_occupancy             (streamFork_3__io_outputs_1_thrown_translated_fifo_io_occupancy[2:0]  ), //o
    .io_clock                 (io_clock                                                             ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                                                )  //i
  );
  assign inputsCmd_0_valid = io_sharedInputs_0_arw_valid;
  assign io_sharedInputs_0_arw_ready = inputsCmd_0_ready;
  assign inputsCmd_0_payload_addr = io_sharedInputs_0_arw_payload_addr;
  assign inputsCmd_0_payload_id = io_sharedInputs_0_arw_payload_id;
  assign inputsCmd_0_payload_len = io_sharedInputs_0_arw_payload_len;
  assign inputsCmd_0_payload_size = io_sharedInputs_0_arw_payload_size;
  assign inputsCmd_0_payload_burst = io_sharedInputs_0_arw_payload_burst;
  assign inputsCmd_0_payload_write = io_sharedInputs_0_arw_payload_write;
  assign inputsCmd_0_ready = cmdArbiter_io_inputs_0_ready;
  assign io_output_arw_valid = streamFork_3__io_outputs_0_valid;
  assign io_output_arw_payload_addr = streamFork_3__io_outputs_0_payload_addr;
  assign io_output_arw_payload_len = streamFork_3__io_outputs_0_payload_len;
  assign io_output_arw_payload_size = streamFork_3__io_outputs_0_payload_size;
  assign io_output_arw_payload_burst = streamFork_3__io_outputs_0_payload_burst;
  assign io_output_arw_payload_write = streamFork_3__io_outputs_0_payload_write;
  assign io_output_arw_payload_id = (streamFork_3__io_outputs_0_payload_write ? streamFork_3__io_outputs_0_payload_id : streamFork_3__io_outputs_0_payload_id);
  always @ (*) begin
    streamFork_3__io_outputs_1_thrown_valid = streamFork_3__io_outputs_1_valid;
    if(_zz_4_)begin
      streamFork_3__io_outputs_1_thrown_valid = 1'b0;
    end
  end

  always @ (*) begin
    _zz_1_ = streamFork_3__io_outputs_1_thrown_ready;
    if(_zz_4_)begin
      _zz_1_ = 1'b1;
    end
  end

  assign streamFork_3__io_outputs_1_thrown_payload_addr = streamFork_3__io_outputs_1_payload_addr;
  assign streamFork_3__io_outputs_1_thrown_payload_id = streamFork_3__io_outputs_1_payload_id;
  assign streamFork_3__io_outputs_1_thrown_payload_len = streamFork_3__io_outputs_1_payload_len;
  assign streamFork_3__io_outputs_1_thrown_payload_size = streamFork_3__io_outputs_1_payload_size;
  assign streamFork_3__io_outputs_1_thrown_payload_burst = streamFork_3__io_outputs_1_payload_burst;
  assign streamFork_3__io_outputs_1_thrown_payload_write = streamFork_3__io_outputs_1_payload_write;
  assign streamFork_3__io_outputs_1_thrown_translated_valid = streamFork_3__io_outputs_1_thrown_valid;
  assign streamFork_3__io_outputs_1_thrown_ready = streamFork_3__io_outputs_1_thrown_translated_ready;
  assign streamFork_3__io_outputs_1_thrown_translated_ready = streamFork_3__io_outputs_1_thrown_translated_fifo_io_push_ready;
  assign writeLogic_routeDataInput_valid = io_sharedInputs_0_w_valid;
  assign writeLogic_routeDataInput_ready = io_sharedInputs_0_w_ready;
  assign writeLogic_routeDataInput_payload_data = io_sharedInputs_0_w_payload_data;
  assign writeLogic_routeDataInput_payload_strb = io_sharedInputs_0_w_payload_strb;
  assign writeLogic_routeDataInput_payload_last = io_sharedInputs_0_w_payload_last;
  assign io_output_w_valid = (streamFork_3__io_outputs_1_thrown_translated_fifo_io_pop_valid && writeLogic_routeDataInput_valid);
  assign io_output_w_payload_data = writeLogic_routeDataInput_payload_data;
  assign io_output_w_payload_strb = writeLogic_routeDataInput_payload_strb;
  assign io_output_w_payload_last = writeLogic_routeDataInput_payload_last;
  assign io_sharedInputs_0_w_ready = ((streamFork_3__io_outputs_1_thrown_translated_fifo_io_pop_valid && io_output_w_ready) && 1'b1);
  assign _zz_2_ = ((io_output_w_valid && io_output_w_ready) && io_output_w_payload_last);
  assign writeLogic_writeRspSels_0 = 1'b1;
  assign io_sharedInputs_0_b_valid = (io_output_b_valid && writeLogic_writeRspSels_0);
  assign io_sharedInputs_0_b_payload_resp = io_output_b_payload_resp;
  assign io_sharedInputs_0_b_payload_id = io_output_b_payload_id;
  assign io_output_b_ready = io_sharedInputs_0_b_ready;
  assign readRspSels_0 = 1'b1;
  assign io_sharedInputs_0_r_valid = (io_output_r_valid && readRspSels_0);
  assign io_sharedInputs_0_r_payload_data = io_output_r_payload_data;
  assign io_sharedInputs_0_r_payload_resp = io_output_r_payload_resp;
  assign io_sharedInputs_0_r_payload_last = io_output_r_payload_last;
  assign io_sharedInputs_0_r_payload_id = io_output_r_payload_id;
  assign io_output_r_ready = io_sharedInputs_0_r_ready;
  assign _zz_3_ = 1'b0;

endmodule

module Apb3MachineTimer (
  input      [11:0]   io_bus_PADDR,
  input      [0:0]    io_bus_PSEL,
  input               io_bus_PENABLE,
  output              io_bus_PREADY,
  input               io_bus_PWRITE,
  input      [31:0]   io_bus_PWDATA,
  output reg [31:0]   io_bus_PRDATA,
  output              io_bus_PSLVERROR,
  output              io_interrupt,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_5_;
  wire       [63:0]   ctrl_io_counter;
  wire                ctrl_io_interrupt;
  wire       [31:0]   _zz_6_;
  wire       [31:0]   _zz_7_;
  wire       [31:0]   _zz_8_;
  wire       [31:0]   _zz_9_;
  wire                _zz_1_;
  reg        [63:0]   mapper_cfg_compare;
  wire       [63:0]   _zz_2_;
  reg                 _zz_3_;
  reg                 _zz_4_;

  assign _zz_6_ = io_bus_PWDATA[31 : 0];
  assign _zz_7_ = _zz_6_;
  assign _zz_8_ = io_bus_PWDATA[31 : 0];
  assign _zz_9_ = _zz_8_;
  MachineTimerCtrl ctrl ( 
    .io_config_compare        (mapper_cfg_compare[63:0]  ), //i
    .io_counter               (ctrl_io_counter[63:0]     ), //o
    .io_clear                 (_zz_5_                    ), //i
    .io_interrupt             (ctrl_io_interrupt         ), //o
    .io_clock                 (io_clock                  ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset     )  //i
  );
  assign io_interrupt = ctrl_io_interrupt;
  assign io_bus_PREADY = 1'b1;
  always @ (*) begin
    io_bus_PRDATA = 32'h0;
    case(io_bus_PADDR)
      12'b000000000000 : begin
        io_bus_PRDATA[31 : 0] = _zz_2_[31 : 0];
      end
      12'b000000000100 : begin
        io_bus_PRDATA[31 : 0] = _zz_2_[63 : 32];
      end
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      default : begin
      end
    endcase
  end

  assign io_bus_PSLVERROR = 1'b0;
  assign _zz_1_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && io_bus_PWRITE);
  assign _zz_2_ = ctrl_io_counter;
  always @ (*) begin
    _zz_3_ = 1'b0;
    case(io_bus_PADDR)
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
        if(_zz_1_)begin
          _zz_3_ = 1'b1;
        end
      end
      12'b000000001100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_4_ = 1'b0;
    case(io_bus_PADDR)
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
        if(_zz_1_)begin
          _zz_4_ = 1'b1;
        end
      end
      default : begin
      end
    endcase
  end

  assign _zz_5_ = (_zz_3_ || _zz_4_);
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      mapper_cfg_compare <= 64'h0;
    end else begin
      case(io_bus_PADDR)
        12'b000000000000 : begin
        end
        12'b000000000100 : begin
        end
        12'b000000001000 : begin
          if(_zz_1_)begin
            mapper_cfg_compare[31 : 0] <= _zz_7_;
          end
        end
        12'b000000001100 : begin
          if(_zz_1_)begin
            mapper_cfg_compare[63 : 32] <= _zz_9_;
          end
        end
        default : begin
        end
      endcase
    end
  end


endmodule

module Apb3Plic (
  input      [15:0]   io_bus_PADDR,
  input      [0:0]    io_bus_PSEL,
  input               io_bus_PENABLE,
  output              io_bus_PREADY,
  input               io_bus_PWRITE,
  input      [31:0]   io_bus_PWDATA,
  output reg [31:0]   io_bus_PRDATA,
  output              io_bus_PSLVERROR,
  output              io_interrupt,
  input      [3:0]    io_sources,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire       [0:0]    _zz_29_;
  wire       [0:0]    _zz_30_;
  wire       [0:0]    _zz_31_;
  wire       [0:0]    _zz_32_;
  wire                _zz_1_;
  reg                 _zz_2_;
  reg                 _zz_3_;
  wire                _zz_4_;
  reg                 _zz_5_;
  reg                 _zz_6_;
  wire                _zz_7_;
  reg                 _zz_8_;
  reg                 _zz_9_;
  wire                _zz_10_;
  reg                 _zz_11_;
  reg                 _zz_12_;
  wire                targets_0_ie_0;
  wire                targets_0_ie_1;
  wire                targets_0_ie_2;
  wire                targets_0_ie_3;
  wire       [0:0]    targets_0_threshold;
  wire       [0:0]    targets_0_requests_0_priority;
  wire       [1:0]    targets_0_requests_0_id;
  wire                targets_0_requests_0_valid;
  wire       [0:0]    targets_0_requests_1_priority;
  wire       [1:0]    targets_0_requests_1_id;
  wire                targets_0_requests_1_valid;
  wire       [0:0]    targets_0_requests_2_priority;
  wire       [1:0]    targets_0_requests_2_id;
  wire                targets_0_requests_2_valid;
  wire       [0:0]    targets_0_requests_3_priority;
  wire       [1:0]    targets_0_requests_3_id;
  wire                targets_0_requests_3_valid;
  wire       [0:0]    targets_0_requests_4_priority;
  wire       [1:0]    targets_0_requests_4_id;
  wire                targets_0_requests_4_valid;
  wire                _zz_13_;
  wire       [0:0]    _zz_14_;
  wire                _zz_15_;
  wire                _zz_16_;
  wire       [0:0]    _zz_17_;
  wire                _zz_18_;
  wire                _zz_19_;
  wire       [0:0]    _zz_20_;
  wire                _zz_21_;
  wire                _zz_22_;
  wire       [0:0]    targets_0_bestRequest_priority;
  wire       [1:0]    targets_0_bestRequest_id;
  wire                targets_0_bestRequest_valid;
  wire                targets_0_iep;
  wire       [1:0]    targets_0_claim;
  wire                _zz_23_;
  wire                _zz_24_;
  reg                 mapping_claim_valid;
  reg        [1:0]    mapping_claim_payload;
  reg                 mapping_completion_valid;
  reg        [1:0]    mapping_completion_payload;
  reg                 mapping_targetMapping_0_targetCompletion_valid;
  wire       [1:0]    mapping_targetMapping_0_targetCompletion_payload;
  reg                 _zz_25_;
  reg                 _zz_26_;
  reg                 _zz_27_;
  reg                 _zz_28_;

  assign _zz_29_ = io_bus_PWDATA[0 : 0];
  assign _zz_30_ = io_bus_PWDATA[1 : 1];
  assign _zz_31_ = io_bus_PWDATA[2 : 2];
  assign _zz_32_ = io_bus_PWDATA[3 : 3];
  assign _zz_1_ = io_sources[0];
  assign _zz_4_ = io_sources[1];
  assign _zz_7_ = io_sources[2];
  assign _zz_10_ = io_sources[3];
  assign targets_0_requests_0_priority = (1'b0);
  assign targets_0_requests_0_id = (2'b00);
  assign targets_0_requests_0_valid = 1'b1;
  assign targets_0_requests_1_priority = (1'b1);
  assign targets_0_requests_1_id = (2'b00);
  assign targets_0_requests_1_valid = (_zz_2_ && targets_0_ie_0);
  assign targets_0_requests_2_priority = (1'b1);
  assign targets_0_requests_2_id = (2'b01);
  assign targets_0_requests_2_valid = (_zz_5_ && targets_0_ie_1);
  assign targets_0_requests_3_priority = (1'b1);
  assign targets_0_requests_3_id = (2'b10);
  assign targets_0_requests_3_valid = (_zz_8_ && targets_0_ie_2);
  assign targets_0_requests_4_priority = (1'b1);
  assign targets_0_requests_4_id = (2'b11);
  assign targets_0_requests_4_valid = (_zz_11_ && targets_0_ie_3);
  assign _zz_13_ = ((! targets_0_requests_1_valid) || (targets_0_requests_0_valid && (targets_0_requests_1_priority <= targets_0_requests_0_priority)));
  assign _zz_14_ = (_zz_13_ ? targets_0_requests_0_priority : targets_0_requests_1_priority);
  assign _zz_15_ = (_zz_13_ ? targets_0_requests_0_valid : targets_0_requests_1_valid);
  assign _zz_16_ = ((! targets_0_requests_3_valid) || (targets_0_requests_2_valid && (targets_0_requests_3_priority <= targets_0_requests_2_priority)));
  assign _zz_17_ = (_zz_16_ ? targets_0_requests_2_priority : targets_0_requests_3_priority);
  assign _zz_18_ = (_zz_16_ ? targets_0_requests_2_valid : targets_0_requests_3_valid);
  assign _zz_19_ = ((! _zz_18_) || (_zz_15_ && (_zz_17_ <= _zz_14_)));
  assign _zz_20_ = (_zz_19_ ? _zz_14_ : _zz_17_);
  assign _zz_21_ = (_zz_19_ ? _zz_15_ : _zz_18_);
  assign _zz_22_ = ((! targets_0_requests_4_valid) || (_zz_21_ && (targets_0_requests_4_priority <= _zz_20_)));
  assign targets_0_bestRequest_priority = (_zz_22_ ? _zz_20_ : targets_0_requests_4_priority);
  assign targets_0_bestRequest_id = (_zz_22_ ? (_zz_19_ ? (_zz_13_ ? targets_0_requests_0_id : targets_0_requests_1_id) : (_zz_16_ ? targets_0_requests_2_id : targets_0_requests_3_id)) : targets_0_requests_4_id);
  assign targets_0_bestRequest_valid = (_zz_22_ ? _zz_21_ : targets_0_requests_4_valid);
  assign targets_0_iep = (targets_0_threshold < targets_0_bestRequest_priority);
  assign targets_0_claim = (targets_0_iep ? targets_0_bestRequest_id : (2'b00));
  assign targets_0_threshold = (1'b0);
  assign io_bus_PREADY = 1'b1;
  always @ (*) begin
    io_bus_PRDATA = 32'h0;
    case(io_bus_PADDR)
      16'b1111000000000100 : begin
        io_bus_PRDATA[1 : 0] = targets_0_claim;
      end
      16'b0010000000000000 : begin
        io_bus_PRDATA[0 : 0] = targets_0_ie_0;
        io_bus_PRDATA[1 : 1] = targets_0_ie_1;
        io_bus_PRDATA[2 : 2] = targets_0_ie_2;
        io_bus_PRDATA[3 : 3] = targets_0_ie_3;
      end
      default : begin
      end
    endcase
  end

  assign io_bus_PSLVERROR = 1'b0;
  assign _zz_23_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && io_bus_PWRITE);
  assign _zz_24_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && (! io_bus_PWRITE));
  always @ (*) begin
    mapping_claim_valid = 1'b0;
    case(io_bus_PADDR)
      16'b1111000000000100 : begin
        if(_zz_24_)begin
          mapping_claim_valid = 1'b1;
        end
      end
      16'b0010000000000000 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    mapping_claim_payload = (2'bxx);
    case(io_bus_PADDR)
      16'b1111000000000100 : begin
        if(_zz_24_)begin
          mapping_claim_payload = targets_0_claim;
        end
      end
      16'b0010000000000000 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    mapping_completion_valid = 1'b0;
    if(mapping_targetMapping_0_targetCompletion_valid)begin
      mapping_completion_valid = 1'b1;
    end
  end

  always @ (*) begin
    mapping_completion_payload = (2'bxx);
    if(mapping_targetMapping_0_targetCompletion_valid)begin
      mapping_completion_payload = mapping_targetMapping_0_targetCompletion_payload;
    end
  end

  always @ (*) begin
    mapping_targetMapping_0_targetCompletion_valid = 1'b0;
    case(io_bus_PADDR)
      16'b1111000000000100 : begin
        if(_zz_23_)begin
          mapping_targetMapping_0_targetCompletion_valid = 1'b1;
        end
      end
      16'b0010000000000000 : begin
      end
      default : begin
      end
    endcase
  end

  assign targets_0_ie_0 = _zz_25_;
  assign targets_0_ie_1 = _zz_26_;
  assign targets_0_ie_2 = _zz_27_;
  assign targets_0_ie_3 = _zz_28_;
  assign io_interrupt = targets_0_iep;
  assign mapping_targetMapping_0_targetCompletion_payload = io_bus_PWDATA[1 : 0];
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      _zz_2_ <= 1'b0;
      _zz_3_ <= 1'b0;
      _zz_5_ <= 1'b0;
      _zz_6_ <= 1'b0;
      _zz_8_ <= 1'b0;
      _zz_9_ <= 1'b0;
      _zz_11_ <= 1'b0;
      _zz_12_ <= 1'b0;
      _zz_25_ <= 1'b0;
      _zz_26_ <= 1'b0;
      _zz_27_ <= 1'b0;
      _zz_28_ <= 1'b0;
    end else begin
      if((! _zz_3_))begin
        _zz_2_ <= _zz_1_;
        _zz_3_ <= _zz_1_;
      end
      if((! _zz_6_))begin
        _zz_5_ <= _zz_4_;
        _zz_6_ <= _zz_4_;
      end
      if((! _zz_9_))begin
        _zz_8_ <= _zz_7_;
        _zz_9_ <= _zz_7_;
      end
      if((! _zz_12_))begin
        _zz_11_ <= _zz_10_;
        _zz_12_ <= _zz_10_;
      end
      if(mapping_claim_valid)begin
        case(mapping_claim_payload)
          2'b00 : begin
            _zz_2_ <= 1'b0;
          end
          2'b01 : begin
            _zz_5_ <= 1'b0;
          end
          2'b10 : begin
            _zz_8_ <= 1'b0;
          end
          default : begin
            _zz_11_ <= 1'b0;
          end
        endcase
      end
      if(mapping_completion_valid)begin
        case(mapping_completion_payload)
          2'b00 : begin
            _zz_3_ <= 1'b0;
          end
          2'b01 : begin
            _zz_6_ <= 1'b0;
          end
          2'b10 : begin
            _zz_9_ <= 1'b0;
          end
          default : begin
            _zz_12_ <= 1'b0;
          end
        endcase
      end
      case(io_bus_PADDR)
        16'b1111000000000100 : begin
        end
        16'b0010000000000000 : begin
          if(_zz_23_)begin
            _zz_25_ <= _zz_29_[0];
            _zz_26_ <= _zz_30_[0];
            _zz_27_ <= _zz_31_[0];
            _zz_28_ <= _zz_32_[0];
          end
        end
        default : begin
        end
      endcase
    end
  end


endmodule

module Apb3Uart (
  input      [11:0]   io_bus_PADDR,
  input      [0:0]    io_bus_PSEL,
  input               io_bus_PENABLE,
  output              io_bus_PREADY,
  input               io_bus_PWRITE,
  input      [31:0]   io_bus_PWDATA,
  output reg [31:0]   io_bus_PRDATA,
  output              io_bus_PSLVERROR,
  output              io_uart_txd,
  input               io_uart_rxd,
  output              io_interrupt,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_6_;
  reg                 _zz_7_;
  wire                _zz_8_;
  reg        [1:0]    _zz_9_;
  reg        [1:0]    _zz_10_;
  wire                ctrl_io_uart_txd;
  wire                ctrl_io_interrupt;
  wire                ctrl_io_write_ready;
  wire                ctrl_io_read_valid;
  wire       [8:0]    ctrl_io_read_payload;
  wire                mapper_tx_streamUnbuffered_queueWithOccupancy_io_push_ready;
  wire                mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_valid;
  wire       [8:0]    mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_payload;
  wire       [4:0]    mapper_tx_streamUnbuffered_queueWithOccupancy_io_occupancy;
  wire       [4:0]    mapper_tx_streamUnbuffered_queueWithOccupancy_io_availability;
  wire                ctrl_io_read_queueWithOccupancy_io_push_ready;
  wire                ctrl_io_read_queueWithOccupancy_io_pop_valid;
  wire       [8:0]    ctrl_io_read_queueWithOccupancy_io_pop_payload;
  wire       [4:0]    ctrl_io_read_queueWithOccupancy_io_occupancy;
  wire       [4:0]    ctrl_io_read_queueWithOccupancy_io_availability;
  wire       [1:0]    mapper_interrupt_irqCtrl_io_pendings;
  wire       [19:0]   _zz_11_;
  wire       [19:0]   _zz_12_;
  wire       [4:0]    _zz_13_;
  wire                _zz_1_;
  wire                _zz_2_;
  reg        [19:0]   mapper_config_cfg_clockDivider;
  reg        `ParityType_defaultEncoding_type mapper_config_frameCfg_parity;
  reg        `StopType_defaultEncoding_type mapper_config_frameCfg_stop;
  reg        [3:0]    mapper_config_frameCfg_dataLength;
  reg                 _zz_3_;
  wire                mapper_tx_streamUnbuffered_valid;
  wire                mapper_tx_streamUnbuffered_ready;
  wire       [8:0]    mapper_tx_streamUnbuffered_payload;
  reg        [1:0]    mapper_interrupt_irqCtrl_io_masks_driver;
  wire       `ParityType_defaultEncoding_type _zz_4_;
  wire       `StopType_defaultEncoding_type _zz_5_;
  `ifndef SYNTHESIS
  reg [31:0] mapper_config_frameCfg_parity_string;
  reg [23:0] mapper_config_frameCfg_stop_string;
  reg [31:0] _zz_4__string;
  reg [23:0] _zz_5__string;
  `endif


  assign _zz_11_ = io_bus_PWDATA[19 : 0];
  assign _zz_12_ = _zz_11_;
  assign _zz_13_ = (5'h10 - mapper_tx_streamUnbuffered_queueWithOccupancy_io_occupancy);
  UartCtrl ctrl ( 
    .io_config_clockDivider       (mapper_config_cfg_clockDivider[19:0]                               ), //i
    .io_frameConfig_parity        (mapper_config_frameCfg_parity[1:0]                                 ), //i
    .io_frameConfig_stop          (mapper_config_frameCfg_stop                                        ), //i
    .io_frameConfig_dataLength    (mapper_config_frameCfg_dataLength[3:0]                             ), //i
    .io_uart_txd                  (ctrl_io_uart_txd                                                   ), //o
    .io_uart_rxd                  (io_uart_rxd                                                        ), //i
    .io_interrupt                 (ctrl_io_interrupt                                                  ), //o
    .io_pendingInterrupts         (mapper_interrupt_irqCtrl_io_pendings[1:0]                          ), //i
    .io_write_valid               (mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_valid         ), //i
    .io_write_ready               (ctrl_io_write_ready                                                ), //o
    .io_write_payload             (mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_payload[8:0]  ), //i
    .io_read_valid                (ctrl_io_read_valid                                                 ), //o
    .io_read_ready                (ctrl_io_read_queueWithOccupancy_io_push_ready                      ), //i
    .io_read_payload              (ctrl_io_read_payload[8:0]                                          ), //o
    .io_clock                     (io_clock                                                           ), //i
    .resetCtrl_systemReset        (resetCtrl_systemReset                                              )  //i
  );
  StreamFifo mapper_tx_streamUnbuffered_queueWithOccupancy ( 
    .io_push_valid            (mapper_tx_streamUnbuffered_valid                                    ), //i
    .io_push_ready            (mapper_tx_streamUnbuffered_queueWithOccupancy_io_push_ready         ), //o
    .io_push_payload          (mapper_tx_streamUnbuffered_payload[8:0]                             ), //i
    .io_pop_valid             (mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_valid          ), //o
    .io_pop_ready             (ctrl_io_write_ready                                                 ), //i
    .io_pop_payload           (mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_payload[8:0]   ), //o
    .io_flush                 (_zz_6_                                                              ), //i
    .io_occupancy             (mapper_tx_streamUnbuffered_queueWithOccupancy_io_occupancy[4:0]     ), //o
    .io_availability          (mapper_tx_streamUnbuffered_queueWithOccupancy_io_availability[4:0]  ), //o
    .io_clock                 (io_clock                                                            ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                                               )  //i
  );
  StreamFifo ctrl_io_read_queueWithOccupancy ( 
    .io_push_valid            (ctrl_io_read_valid                                    ), //i
    .io_push_ready            (ctrl_io_read_queueWithOccupancy_io_push_ready         ), //o
    .io_push_payload          (ctrl_io_read_payload[8:0]                             ), //i
    .io_pop_valid             (ctrl_io_read_queueWithOccupancy_io_pop_valid          ), //o
    .io_pop_ready             (_zz_7_                                                ), //i
    .io_pop_payload           (ctrl_io_read_queueWithOccupancy_io_pop_payload[8:0]   ), //o
    .io_flush                 (_zz_8_                                                ), //i
    .io_occupancy             (ctrl_io_read_queueWithOccupancy_io_occupancy[4:0]     ), //o
    .io_availability          (ctrl_io_read_queueWithOccupancy_io_availability[4:0]  ), //o
    .io_clock                 (io_clock                                              ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                                 )  //i
  );
  InterruptCtrl mapper_interrupt_irqCtrl ( 
    .io_inputs                (_zz_9_[1:0]                                    ), //i
    .io_clears                (_zz_10_[1:0]                                   ), //i
    .io_masks                 (mapper_interrupt_irqCtrl_io_masks_driver[1:0]  ), //i
    .io_pendings              (mapper_interrupt_irqCtrl_io_pendings[1:0]      ), //o
    .io_clock                 (io_clock                                       ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                          )  //i
  );
  `ifndef SYNTHESIS
  always @(*) begin
    case(mapper_config_frameCfg_parity)
      `ParityType_defaultEncoding_NONE : mapper_config_frameCfg_parity_string = "NONE";
      `ParityType_defaultEncoding_EVEN : mapper_config_frameCfg_parity_string = "EVEN";
      `ParityType_defaultEncoding_ODD : mapper_config_frameCfg_parity_string = "ODD ";
      default : mapper_config_frameCfg_parity_string = "????";
    endcase
  end
  always @(*) begin
    case(mapper_config_frameCfg_stop)
      `StopType_defaultEncoding_ONE : mapper_config_frameCfg_stop_string = "ONE";
      `StopType_defaultEncoding_TWO : mapper_config_frameCfg_stop_string = "TWO";
      default : mapper_config_frameCfg_stop_string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_4_)
      `ParityType_defaultEncoding_NONE : _zz_4__string = "NONE";
      `ParityType_defaultEncoding_EVEN : _zz_4__string = "EVEN";
      `ParityType_defaultEncoding_ODD : _zz_4__string = "ODD ";
      default : _zz_4__string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_5_)
      `StopType_defaultEncoding_ONE : _zz_5__string = "ONE";
      `StopType_defaultEncoding_TWO : _zz_5__string = "TWO";
      default : _zz_5__string = "???";
    endcase
  end
  `endif

  assign io_uart_txd = ctrl_io_uart_txd;
  assign io_interrupt = ctrl_io_interrupt;
  assign io_bus_PREADY = 1'b1;
  always @ (*) begin
    io_bus_PRDATA = 32'h0;
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000000000 : begin
        io_bus_PRDATA[16 : 16] = (ctrl_io_read_queueWithOccupancy_io_pop_valid ^ 1'b0);
        io_bus_PRDATA[8 : 0] = ctrl_io_read_queueWithOccupancy_io_pop_payload;
      end
      12'b000000000100 : begin
        io_bus_PRDATA[20 : 16] = _zz_13_;
        io_bus_PRDATA[28 : 24] = ctrl_io_read_queueWithOccupancy_io_occupancy;
      end
      12'b000000010000 : begin
        io_bus_PRDATA[1 : 0] = mapper_interrupt_irqCtrl_io_pendings;
      end
      12'b000000010100 : begin
        io_bus_PRDATA[1 : 0] = mapper_interrupt_irqCtrl_io_masks_driver;
      end
      default : begin
      end
    endcase
  end

  assign io_bus_PSLVERROR = 1'b0;
  assign _zz_1_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && io_bus_PWRITE);
  assign _zz_2_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && (! io_bus_PWRITE));
  always @ (*) begin
    _zz_3_ = 1'b0;
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000000000 : begin
        if(_zz_1_)begin
          _zz_3_ = 1'b1;
        end
      end
      12'b000000000100 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      default : begin
      end
    endcase
  end

  assign mapper_tx_streamUnbuffered_valid = _zz_3_;
  assign mapper_tx_streamUnbuffered_payload = io_bus_PWDATA[8 : 0];
  assign mapper_tx_streamUnbuffered_ready = mapper_tx_streamUnbuffered_queueWithOccupancy_io_push_ready;
  always @ (*) begin
    _zz_7_ = 1'b0;
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000000000 : begin
        if(_zz_2_)begin
          _zz_7_ = 1'b1;
        end
      end
      12'b000000000100 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_10_ = (2'b00);
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000010000 : begin
        if(_zz_1_)begin
          _zz_10_ = io_bus_PWDATA[1 : 0];
        end
      end
      12'b000000010100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_9_[0] = (! mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_valid);
    _zz_9_[1] = ctrl_io_read_valid;
  end

  assign _zz_4_ = io_bus_PWDATA[9 : 8];
  assign _zz_5_ = io_bus_PWDATA[16 : 16];
  assign _zz_6_ = 1'b0;
  assign _zz_8_ = 1'b0;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      mapper_config_cfg_clockDivider <= 20'h0006b;
      mapper_config_frameCfg_dataLength <= (4'b0111);
      mapper_config_frameCfg_parity <= `ParityType_defaultEncoding_NONE;
      mapper_config_frameCfg_stop <= `StopType_defaultEncoding_ONE;
      mapper_interrupt_irqCtrl_io_masks_driver <= (2'b00);
    end else begin
      case(io_bus_PADDR)
        12'b000000001000 : begin
          if(_zz_1_)begin
            mapper_config_cfg_clockDivider[19 : 0] <= _zz_12_;
          end
        end
        12'b000000001100 : begin
          if(_zz_1_)begin
            mapper_config_frameCfg_dataLength <= io_bus_PWDATA[3 : 0];
            mapper_config_frameCfg_parity <= _zz_4_;
            mapper_config_frameCfg_stop <= _zz_5_;
          end
        end
        12'b000000000000 : begin
        end
        12'b000000000100 : begin
        end
        12'b000000010000 : begin
        end
        12'b000000010100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqCtrl_io_masks_driver <= io_bus_PWDATA[1 : 0];
          end
        end
        default : begin
        end
      endcase
    end
  end


endmodule

module Apb3Uart_1_ (
  input      [11:0]   io_bus_PADDR,
  input      [0:0]    io_bus_PSEL,
  input               io_bus_PENABLE,
  output              io_bus_PREADY,
  input               io_bus_PWRITE,
  input      [31:0]   io_bus_PWDATA,
  output reg [31:0]   io_bus_PRDATA,
  output              io_bus_PSLVERROR,
  output              io_uart_txd,
  input               io_uart_rxd,
  output              io_interrupt,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_6_;
  reg                 _zz_7_;
  wire                _zz_8_;
  reg        [1:0]    _zz_9_;
  reg        [1:0]    _zz_10_;
  wire                ctrl_io_uart_txd;
  wire                ctrl_io_interrupt;
  wire                ctrl_io_write_ready;
  wire                ctrl_io_read_valid;
  wire       [8:0]    ctrl_io_read_payload;
  wire                mapper_tx_streamUnbuffered_queueWithOccupancy_io_push_ready;
  wire                mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_valid;
  wire       [8:0]    mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_payload;
  wire       [6:0]    mapper_tx_streamUnbuffered_queueWithOccupancy_io_occupancy;
  wire       [6:0]    mapper_tx_streamUnbuffered_queueWithOccupancy_io_availability;
  wire                ctrl_io_read_queueWithOccupancy_io_push_ready;
  wire                ctrl_io_read_queueWithOccupancy_io_pop_valid;
  wire       [8:0]    ctrl_io_read_queueWithOccupancy_io_pop_payload;
  wire       [6:0]    ctrl_io_read_queueWithOccupancy_io_occupancy;
  wire       [6:0]    ctrl_io_read_queueWithOccupancy_io_availability;
  wire       [1:0]    mapper_interrupt_irqCtrl_io_pendings;
  wire       [19:0]   _zz_11_;
  wire       [19:0]   _zz_12_;
  wire       [6:0]    _zz_13_;
  wire                _zz_1_;
  wire                _zz_2_;
  reg        [19:0]   mapper_config_cfg_clockDivider;
  reg        `ParityType_defaultEncoding_type mapper_config_frameCfg_parity;
  reg        `StopType_defaultEncoding_type mapper_config_frameCfg_stop;
  reg        [3:0]    mapper_config_frameCfg_dataLength;
  reg                 _zz_3_;
  wire                mapper_tx_streamUnbuffered_valid;
  wire                mapper_tx_streamUnbuffered_ready;
  wire       [8:0]    mapper_tx_streamUnbuffered_payload;
  reg        [1:0]    mapper_interrupt_irqCtrl_io_masks_driver;
  wire       `ParityType_defaultEncoding_type _zz_4_;
  wire       `StopType_defaultEncoding_type _zz_5_;
  `ifndef SYNTHESIS
  reg [31:0] mapper_config_frameCfg_parity_string;
  reg [23:0] mapper_config_frameCfg_stop_string;
  reg [31:0] _zz_4__string;
  reg [23:0] _zz_5__string;
  `endif


  assign _zz_11_ = io_bus_PWDATA[19 : 0];
  assign _zz_12_ = _zz_11_;
  assign _zz_13_ = (7'h40 - mapper_tx_streamUnbuffered_queueWithOccupancy_io_occupancy);
  UartCtrl ctrl ( 
    .io_config_clockDivider       (mapper_config_cfg_clockDivider[19:0]                               ), //i
    .io_frameConfig_parity        (mapper_config_frameCfg_parity[1:0]                                 ), //i
    .io_frameConfig_stop          (mapper_config_frameCfg_stop                                        ), //i
    .io_frameConfig_dataLength    (mapper_config_frameCfg_dataLength[3:0]                             ), //i
    .io_uart_txd                  (ctrl_io_uart_txd                                                   ), //o
    .io_uart_rxd                  (io_uart_rxd                                                        ), //i
    .io_interrupt                 (ctrl_io_interrupt                                                  ), //o
    .io_pendingInterrupts         (mapper_interrupt_irqCtrl_io_pendings[1:0]                          ), //i
    .io_write_valid               (mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_valid         ), //i
    .io_write_ready               (ctrl_io_write_ready                                                ), //o
    .io_write_payload             (mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_payload[8:0]  ), //i
    .io_read_valid                (ctrl_io_read_valid                                                 ), //o
    .io_read_ready                (ctrl_io_read_queueWithOccupancy_io_push_ready                      ), //i
    .io_read_payload              (ctrl_io_read_payload[8:0]                                          ), //o
    .io_clock                     (io_clock                                                           ), //i
    .resetCtrl_systemReset        (resetCtrl_systemReset                                              )  //i
  );
  StreamFifo_2_ mapper_tx_streamUnbuffered_queueWithOccupancy ( 
    .io_push_valid            (mapper_tx_streamUnbuffered_valid                                    ), //i
    .io_push_ready            (mapper_tx_streamUnbuffered_queueWithOccupancy_io_push_ready         ), //o
    .io_push_payload          (mapper_tx_streamUnbuffered_payload[8:0]                             ), //i
    .io_pop_valid             (mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_valid          ), //o
    .io_pop_ready             (ctrl_io_write_ready                                                 ), //i
    .io_pop_payload           (mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_payload[8:0]   ), //o
    .io_flush                 (_zz_6_                                                              ), //i
    .io_occupancy             (mapper_tx_streamUnbuffered_queueWithOccupancy_io_occupancy[6:0]     ), //o
    .io_availability          (mapper_tx_streamUnbuffered_queueWithOccupancy_io_availability[6:0]  ), //o
    .io_clock                 (io_clock                                                            ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                                               )  //i
  );
  StreamFifo_2_ ctrl_io_read_queueWithOccupancy ( 
    .io_push_valid            (ctrl_io_read_valid                                    ), //i
    .io_push_ready            (ctrl_io_read_queueWithOccupancy_io_push_ready         ), //o
    .io_push_payload          (ctrl_io_read_payload[8:0]                             ), //i
    .io_pop_valid             (ctrl_io_read_queueWithOccupancy_io_pop_valid          ), //o
    .io_pop_ready             (_zz_7_                                                ), //i
    .io_pop_payload           (ctrl_io_read_queueWithOccupancy_io_pop_payload[8:0]   ), //o
    .io_flush                 (_zz_8_                                                ), //i
    .io_occupancy             (ctrl_io_read_queueWithOccupancy_io_occupancy[6:0]     ), //o
    .io_availability          (ctrl_io_read_queueWithOccupancy_io_availability[6:0]  ), //o
    .io_clock                 (io_clock                                              ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                                 )  //i
  );
  InterruptCtrl mapper_interrupt_irqCtrl ( 
    .io_inputs                (_zz_9_[1:0]                                    ), //i
    .io_clears                (_zz_10_[1:0]                                   ), //i
    .io_masks                 (mapper_interrupt_irqCtrl_io_masks_driver[1:0]  ), //i
    .io_pendings              (mapper_interrupt_irqCtrl_io_pendings[1:0]      ), //o
    .io_clock                 (io_clock                                       ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                          )  //i
  );
  `ifndef SYNTHESIS
  always @(*) begin
    case(mapper_config_frameCfg_parity)
      `ParityType_defaultEncoding_NONE : mapper_config_frameCfg_parity_string = "NONE";
      `ParityType_defaultEncoding_EVEN : mapper_config_frameCfg_parity_string = "EVEN";
      `ParityType_defaultEncoding_ODD : mapper_config_frameCfg_parity_string = "ODD ";
      default : mapper_config_frameCfg_parity_string = "????";
    endcase
  end
  always @(*) begin
    case(mapper_config_frameCfg_stop)
      `StopType_defaultEncoding_ONE : mapper_config_frameCfg_stop_string = "ONE";
      `StopType_defaultEncoding_TWO : mapper_config_frameCfg_stop_string = "TWO";
      default : mapper_config_frameCfg_stop_string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_4_)
      `ParityType_defaultEncoding_NONE : _zz_4__string = "NONE";
      `ParityType_defaultEncoding_EVEN : _zz_4__string = "EVEN";
      `ParityType_defaultEncoding_ODD : _zz_4__string = "ODD ";
      default : _zz_4__string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_5_)
      `StopType_defaultEncoding_ONE : _zz_5__string = "ONE";
      `StopType_defaultEncoding_TWO : _zz_5__string = "TWO";
      default : _zz_5__string = "???";
    endcase
  end
  `endif

  assign io_uart_txd = ctrl_io_uart_txd;
  assign io_interrupt = ctrl_io_interrupt;
  assign io_bus_PREADY = 1'b1;
  always @ (*) begin
    io_bus_PRDATA = 32'h0;
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000000000 : begin
        io_bus_PRDATA[16 : 16] = (ctrl_io_read_queueWithOccupancy_io_pop_valid ^ 1'b0);
        io_bus_PRDATA[8 : 0] = ctrl_io_read_queueWithOccupancy_io_pop_payload;
      end
      12'b000000000100 : begin
        io_bus_PRDATA[22 : 16] = _zz_13_;
        io_bus_PRDATA[30 : 24] = ctrl_io_read_queueWithOccupancy_io_occupancy;
      end
      12'b000000010000 : begin
        io_bus_PRDATA[1 : 0] = mapper_interrupt_irqCtrl_io_pendings;
      end
      12'b000000010100 : begin
        io_bus_PRDATA[1 : 0] = mapper_interrupt_irqCtrl_io_masks_driver;
      end
      default : begin
      end
    endcase
  end

  assign io_bus_PSLVERROR = 1'b0;
  assign _zz_1_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && io_bus_PWRITE);
  assign _zz_2_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && (! io_bus_PWRITE));
  always @ (*) begin
    _zz_3_ = 1'b0;
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000000000 : begin
        if(_zz_1_)begin
          _zz_3_ = 1'b1;
        end
      end
      12'b000000000100 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      default : begin
      end
    endcase
  end

  assign mapper_tx_streamUnbuffered_valid = _zz_3_;
  assign mapper_tx_streamUnbuffered_payload = io_bus_PWDATA[8 : 0];
  assign mapper_tx_streamUnbuffered_ready = mapper_tx_streamUnbuffered_queueWithOccupancy_io_push_ready;
  always @ (*) begin
    _zz_7_ = 1'b0;
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000000000 : begin
        if(_zz_2_)begin
          _zz_7_ = 1'b1;
        end
      end
      12'b000000000100 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_10_ = (2'b00);
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000010000 : begin
        if(_zz_1_)begin
          _zz_10_ = io_bus_PWDATA[1 : 0];
        end
      end
      12'b000000010100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_9_[0] = (! mapper_tx_streamUnbuffered_queueWithOccupancy_io_pop_valid);
    _zz_9_[1] = ctrl_io_read_valid;
  end

  assign _zz_4_ = io_bus_PWDATA[9 : 8];
  assign _zz_5_ = io_bus_PWDATA[16 : 16];
  assign _zz_6_ = 1'b0;
  assign _zz_8_ = 1'b0;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      mapper_config_cfg_clockDivider <= 20'h0006b;
      mapper_config_frameCfg_dataLength <= (4'b0111);
      mapper_config_frameCfg_parity <= `ParityType_defaultEncoding_NONE;
      mapper_config_frameCfg_stop <= `StopType_defaultEncoding_ONE;
      mapper_interrupt_irqCtrl_io_masks_driver <= (2'b00);
    end else begin
      case(io_bus_PADDR)
        12'b000000001000 : begin
          if(_zz_1_)begin
            mapper_config_cfg_clockDivider[19 : 0] <= _zz_12_;
          end
        end
        12'b000000001100 : begin
          if(_zz_1_)begin
            mapper_config_frameCfg_dataLength <= io_bus_PWDATA[3 : 0];
            mapper_config_frameCfg_parity <= _zz_4_;
            mapper_config_frameCfg_stop <= _zz_5_;
          end
        end
        12'b000000000000 : begin
        end
        12'b000000000100 : begin
        end
        12'b000000010000 : begin
        end
        12'b000000010100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqCtrl_io_masks_driver <= io_bus_PWDATA[1 : 0];
          end
        end
        default : begin
        end
      endcase
    end
  end


endmodule
//Apb3Uart_2_ replaced by Apb3Uart_1_

module Apb3Gpio (
  input      [11:0]   io_bus_PADDR,
  input      [0:0]    io_bus_PSEL,
  input               io_bus_PENABLE,
  output              io_bus_PREADY,
  input               io_bus_PWRITE,
  input      [31:0]   io_bus_PWDATA,
  output reg [31:0]   io_bus_PRDATA,
  output              io_bus_PSLVERROR,
  input      [2:0]    io_gpio_pins_read,
  output     [2:0]    io_gpio_pins_write,
  output     [2:0]    io_gpio_pins_writeEnable,
  output     [2:0]    io_interrupt,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [2:0]    _zz_7_;
  reg        [2:0]    _zz_8_;
  wire       [2:0]    _zz_9_;
  wire       [2:0]    _zz_10_;
  wire       [2:0]    _zz_11_;
  wire       [2:0]    _zz_12_;
  reg        [2:0]    _zz_13_;
  reg        [2:0]    _zz_14_;
  reg        [2:0]    _zz_15_;
  reg        [2:0]    _zz_16_;
  reg        [2:0]    _zz_17_;
  reg        [2:0]    _zz_18_;
  reg        [2:0]    _zz_19_;
  reg        [2:0]    _zz_20_;
  wire       [2:0]    ctrl_io_gpio_pins_write;
  wire       [2:0]    ctrl_io_gpio_pins_writeEnable;
  wire       [2:0]    ctrl_io_value;
  wire       [2:0]    ctrl_io_interrupt;
  wire       [2:0]    ctrl_io_irqHigh_valid;
  wire       [2:0]    ctrl_io_irqLow_valid;
  wire       [2:0]    ctrl_io_irqRise_valid;
  wire       [2:0]    ctrl_io_irqFall_valid;
  wire       [2:0]    mapper_interrupt_irqHighCtrl_io_pendings;
  wire       [2:0]    mapper_interrupt_irqLowCtrl_io_pendings;
  wire       [2:0]    mapper_interrupt_irqRiseCtrl_io_pendings;
  wire       [2:0]    mapper_interrupt_irqFallCtrl_io_pendings;
  wire       [0:0]    _zz_21_;
  wire       [0:0]    _zz_22_;
  wire       [0:0]    _zz_23_;
  wire       [0:0]    _zz_24_;
  wire       [0:0]    _zz_25_;
  wire                _zz_1_;
  reg                 _zz_2_;
  reg                 _zz_3_;
  reg                 _zz_4_;
  reg                 _zz_5_;
  reg                 _zz_6_;
  reg        [2:0]    mapper_interrupt_irqHighCtrl_io_masks_driver;
  reg        [2:0]    mapper_interrupt_irqLowCtrl_io_masks_driver;
  reg        [2:0]    mapper_interrupt_irqRiseCtrl_io_masks_driver;
  reg        [2:0]    mapper_interrupt_irqFallCtrl_io_masks_driver;

  assign _zz_21_ = io_bus_PWDATA[0 : 0];
  assign _zz_22_ = io_bus_PWDATA[1 : 1];
  assign _zz_23_ = io_bus_PWDATA[2 : 2];
  assign _zz_24_ = io_bus_PWDATA[1 : 1];
  assign _zz_25_ = io_bus_PWDATA[2 : 2];
  GpioCtrl ctrl ( 
    .io_gpio_pins_read           (io_gpio_pins_read[2:0]                         ), //i
    .io_gpio_pins_write          (ctrl_io_gpio_pins_write[2:0]                   ), //o
    .io_gpio_pins_writeEnable    (ctrl_io_gpio_pins_writeEnable[2:0]             ), //o
    .io_config_write             (_zz_7_[2:0]                                    ), //i
    .io_config_direction         (_zz_8_[2:0]                                    ), //i
    .io_value                    (ctrl_io_value[2:0]                             ), //o
    .io_enable_high              (_zz_9_[2:0]                                    ), //i
    .io_enable_low               (_zz_10_[2:0]                                   ), //i
    .io_enable_rise              (_zz_11_[2:0]                                   ), //i
    .io_enable_fall              (_zz_12_[2:0]                                   ), //i
    .io_interrupt                (ctrl_io_interrupt[2:0]                         ), //o
    .io_irqHigh_valid            (ctrl_io_irqHigh_valid[2:0]                     ), //o
    .io_irqHigh_pending          (mapper_interrupt_irqHighCtrl_io_pendings[2:0]  ), //i
    .io_irqLow_valid             (ctrl_io_irqLow_valid[2:0]                      ), //o
    .io_irqLow_pending           (mapper_interrupt_irqLowCtrl_io_pendings[2:0]   ), //i
    .io_irqRise_valid            (ctrl_io_irqRise_valid[2:0]                     ), //o
    .io_irqRise_pending          (mapper_interrupt_irqRiseCtrl_io_pendings[2:0]  ), //i
    .io_irqFall_valid            (ctrl_io_irqFall_valid[2:0]                     ), //o
    .io_irqFall_pending          (mapper_interrupt_irqFallCtrl_io_pendings[2:0]  ), //i
    .io_clock                    (io_clock                                       ), //i
    .resetCtrl_systemReset       (resetCtrl_systemReset                          )  //i
  );
  InterruptCtrl_3_ mapper_interrupt_irqHighCtrl ( 
    .io_inputs                (_zz_13_[2:0]                                       ), //i
    .io_clears                (_zz_14_[2:0]                                       ), //i
    .io_masks                 (mapper_interrupt_irqHighCtrl_io_masks_driver[2:0]  ), //i
    .io_pendings              (mapper_interrupt_irqHighCtrl_io_pendings[2:0]      ), //o
    .io_clock                 (io_clock                                           ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                              )  //i
  );
  InterruptCtrl_3_ mapper_interrupt_irqLowCtrl ( 
    .io_inputs                (_zz_15_[2:0]                                      ), //i
    .io_clears                (_zz_16_[2:0]                                      ), //i
    .io_masks                 (mapper_interrupt_irqLowCtrl_io_masks_driver[2:0]  ), //i
    .io_pendings              (mapper_interrupt_irqLowCtrl_io_pendings[2:0]      ), //o
    .io_clock                 (io_clock                                          ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                             )  //i
  );
  InterruptCtrl_3_ mapper_interrupt_irqRiseCtrl ( 
    .io_inputs                (_zz_17_[2:0]                                       ), //i
    .io_clears                (_zz_18_[2:0]                                       ), //i
    .io_masks                 (mapper_interrupt_irqRiseCtrl_io_masks_driver[2:0]  ), //i
    .io_pendings              (mapper_interrupt_irqRiseCtrl_io_pendings[2:0]      ), //o
    .io_clock                 (io_clock                                           ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                              )  //i
  );
  InterruptCtrl_3_ mapper_interrupt_irqFallCtrl ( 
    .io_inputs                (_zz_19_[2:0]                                       ), //i
    .io_clears                (_zz_20_[2:0]                                       ), //i
    .io_masks                 (mapper_interrupt_irqFallCtrl_io_masks_driver[2:0]  ), //i
    .io_pendings              (mapper_interrupt_irqFallCtrl_io_pendings[2:0]      ), //o
    .io_clock                 (io_clock                                           ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                              )  //i
  );
  assign io_gpio_pins_write = ctrl_io_gpio_pins_write;
  assign io_gpio_pins_writeEnable = ctrl_io_gpio_pins_writeEnable;
  assign io_interrupt = ctrl_io_interrupt;
  assign io_bus_PREADY = 1'b1;
  always @ (*) begin
    io_bus_PRDATA = 32'h0;
    case(io_bus_PADDR)
      12'b000000000100 : begin
        io_bus_PRDATA[0 : 0] = _zz_2_;
        io_bus_PRDATA[1 : 1] = _zz_3_;
        io_bus_PRDATA[2 : 2] = _zz_5_;
      end
      12'b000000001000 : begin
        io_bus_PRDATA[0 : 0] = 1'b1;
        io_bus_PRDATA[1 : 1] = _zz_4_;
        io_bus_PRDATA[2 : 2] = _zz_6_;
      end
      12'b000000000000 : begin
        io_bus_PRDATA[1 : 1] = ctrl_io_value[1];
        io_bus_PRDATA[2 : 2] = ctrl_io_value[2];
      end
      12'b000000010000 : begin
        io_bus_PRDATA[2 : 0] = mapper_interrupt_irqHighCtrl_io_pendings;
      end
      12'b000000010100 : begin
        io_bus_PRDATA[2 : 0] = mapper_interrupt_irqHighCtrl_io_masks_driver;
      end
      12'b000000011000 : begin
        io_bus_PRDATA[2 : 0] = mapper_interrupt_irqLowCtrl_io_pendings;
      end
      12'b000000011100 : begin
        io_bus_PRDATA[2 : 0] = mapper_interrupt_irqLowCtrl_io_masks_driver;
      end
      12'b000000100000 : begin
        io_bus_PRDATA[2 : 0] = mapper_interrupt_irqRiseCtrl_io_pendings;
      end
      12'b000000100100 : begin
        io_bus_PRDATA[2 : 0] = mapper_interrupt_irqRiseCtrl_io_masks_driver;
      end
      12'b000000101000 : begin
        io_bus_PRDATA[2 : 0] = mapper_interrupt_irqFallCtrl_io_pendings;
      end
      12'b000000101100 : begin
        io_bus_PRDATA[2 : 0] = mapper_interrupt_irqFallCtrl_io_masks_driver;
      end
      default : begin
      end
    endcase
  end

  assign io_bus_PSLVERROR = 1'b0;
  assign _zz_1_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && io_bus_PWRITE);
  always @ (*) begin
    _zz_7_[0] = _zz_2_;
    _zz_7_[1] = _zz_3_;
    _zz_7_[2] = _zz_5_;
  end

  always @ (*) begin
    _zz_8_[0] = 1'b1;
    _zz_8_[1] = _zz_4_;
    _zz_8_[2] = _zz_6_;
  end

  always @ (*) begin
    _zz_14_ = (3'b000);
    case(io_bus_PADDR)
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
      end
      12'b000000000000 : begin
      end
      12'b000000010000 : begin
        if(_zz_1_)begin
          _zz_14_ = io_bus_PWDATA[2 : 0];
        end
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
      end
      12'b000000100100 : begin
      end
      12'b000000101000 : begin
      end
      12'b000000101100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_16_ = (3'b000);
    case(io_bus_PADDR)
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
      end
      12'b000000000000 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
        if(_zz_1_)begin
          _zz_16_ = io_bus_PWDATA[2 : 0];
        end
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
      end
      12'b000000100100 : begin
      end
      12'b000000101000 : begin
      end
      12'b000000101100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_18_ = (3'b000);
    case(io_bus_PADDR)
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
      end
      12'b000000000000 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
        if(_zz_1_)begin
          _zz_18_ = io_bus_PWDATA[2 : 0];
        end
      end
      12'b000000100100 : begin
      end
      12'b000000101000 : begin
      end
      12'b000000101100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_20_ = (3'b000);
    case(io_bus_PADDR)
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
      end
      12'b000000000000 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
      end
      12'b000000100100 : begin
      end
      12'b000000101000 : begin
        if(_zz_1_)begin
          _zz_20_ = io_bus_PWDATA[2 : 0];
        end
      end
      12'b000000101100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_13_[0] = ctrl_io_irqHigh_valid[0];
    _zz_13_[1] = 1'b0;
    _zz_13_[2] = 1'b0;
  end

  always @ (*) begin
    _zz_15_[0] = ctrl_io_irqLow_valid[0];
    _zz_15_[1] = 1'b0;
    _zz_15_[2] = 1'b0;
  end

  always @ (*) begin
    _zz_17_[0] = ctrl_io_irqRise_valid[0];
    _zz_17_[1] = 1'b0;
    _zz_17_[2] = 1'b0;
  end

  always @ (*) begin
    _zz_19_[0] = ctrl_io_irqFall_valid[0];
    _zz_19_[1] = 1'b0;
    _zz_19_[2] = 1'b0;
  end

  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      _zz_2_ <= 1'b0;
      _zz_3_ <= 1'b0;
      _zz_4_ <= 1'b0;
      _zz_5_ <= 1'b0;
      _zz_6_ <= 1'b0;
      mapper_interrupt_irqHighCtrl_io_masks_driver <= (3'b000);
      mapper_interrupt_irqLowCtrl_io_masks_driver <= (3'b000);
      mapper_interrupt_irqRiseCtrl_io_masks_driver <= (3'b000);
      mapper_interrupt_irqFallCtrl_io_masks_driver <= (3'b000);
    end else begin
      case(io_bus_PADDR)
        12'b000000000100 : begin
          if(_zz_1_)begin
            _zz_2_ <= _zz_21_[0];
            _zz_3_ <= _zz_22_[0];
            _zz_5_ <= _zz_23_[0];
          end
        end
        12'b000000001000 : begin
          if(_zz_1_)begin
            _zz_4_ <= _zz_24_[0];
            _zz_6_ <= _zz_25_[0];
          end
        end
        12'b000000000000 : begin
        end
        12'b000000010000 : begin
        end
        12'b000000010100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqHighCtrl_io_masks_driver <= io_bus_PWDATA[2 : 0];
          end
        end
        12'b000000011000 : begin
        end
        12'b000000011100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqLowCtrl_io_masks_driver <= io_bus_PWDATA[2 : 0];
          end
        end
        12'b000000100000 : begin
        end
        12'b000000100100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqRiseCtrl_io_masks_driver <= io_bus_PWDATA[2 : 0];
          end
        end
        12'b000000101000 : begin
        end
        12'b000000101100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqFallCtrl_io_masks_driver <= io_bus_PWDATA[2 : 0];
          end
        end
        default : begin
        end
      endcase
    end
  end


endmodule

module Apb3Gpio_1_ (
  input      [11:0]   io_bus_PADDR,
  input      [0:0]    io_bus_PSEL,
  input               io_bus_PENABLE,
  output              io_bus_PREADY,
  input               io_bus_PWRITE,
  input      [31:0]   io_bus_PWDATA,
  output reg [31:0]   io_bus_PRDATA,
  output              io_bus_PSLVERROR,
  input      [11:0]   io_gpio_pins_read,
  output     [11:0]   io_gpio_pins_write,
  output     [11:0]   io_gpio_pins_writeEnable,
  output     [11:0]   io_interrupt,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg        [11:0]   _zz_25_;
  reg        [11:0]   _zz_26_;
  wire       [11:0]   _zz_27_;
  wire       [11:0]   _zz_28_;
  wire       [11:0]   _zz_29_;
  wire       [11:0]   _zz_30_;
  reg        [11:0]   _zz_31_;
  reg        [11:0]   _zz_32_;
  reg        [11:0]   _zz_33_;
  reg        [11:0]   _zz_34_;
  reg        [11:0]   _zz_35_;
  reg        [11:0]   _zz_36_;
  reg        [11:0]   _zz_37_;
  reg        [11:0]   _zz_38_;
  wire       [11:0]   ctrl_io_gpio_pins_write;
  wire       [11:0]   ctrl_io_gpio_pins_writeEnable;
  wire       [11:0]   ctrl_io_value;
  wire       [11:0]   ctrl_io_interrupt;
  wire       [11:0]   ctrl_io_irqHigh_valid;
  wire       [11:0]   ctrl_io_irqLow_valid;
  wire       [11:0]   ctrl_io_irqRise_valid;
  wire       [11:0]   ctrl_io_irqFall_valid;
  wire       [11:0]   mapper_interrupt_irqHighCtrl_io_pendings;
  wire       [11:0]   mapper_interrupt_irqLowCtrl_io_pendings;
  wire       [11:0]   mapper_interrupt_irqRiseCtrl_io_pendings;
  wire       [11:0]   mapper_interrupt_irqFallCtrl_io_pendings;
  wire       [0:0]    _zz_39_;
  wire       [0:0]    _zz_40_;
  wire       [0:0]    _zz_41_;
  wire       [0:0]    _zz_42_;
  wire       [0:0]    _zz_43_;
  wire       [0:0]    _zz_44_;
  wire       [0:0]    _zz_45_;
  wire       [0:0]    _zz_46_;
  wire       [0:0]    _zz_47_;
  wire       [0:0]    _zz_48_;
  wire       [0:0]    _zz_49_;
  wire       [0:0]    _zz_50_;
  wire       [0:0]    _zz_51_;
  wire       [0:0]    _zz_52_;
  wire       [0:0]    _zz_53_;
  wire       [0:0]    _zz_54_;
  wire       [0:0]    _zz_55_;
  wire       [0:0]    _zz_56_;
  wire       [0:0]    _zz_57_;
  wire       [0:0]    _zz_58_;
  wire       [0:0]    _zz_59_;
  wire       [0:0]    _zz_60_;
  wire       [0:0]    _zz_61_;
  wire                _zz_1_;
  reg                 _zz_2_;
  reg                 _zz_3_;
  reg                 _zz_4_;
  reg                 _zz_5_;
  reg                 _zz_6_;
  reg                 _zz_7_;
  reg                 _zz_8_;
  reg                 _zz_9_;
  reg                 _zz_10_;
  reg                 _zz_11_;
  reg                 _zz_12_;
  reg                 _zz_13_;
  reg                 _zz_14_;
  reg                 _zz_15_;
  reg                 _zz_16_;
  reg                 _zz_17_;
  reg                 _zz_18_;
  reg                 _zz_19_;
  reg                 _zz_20_;
  reg                 _zz_21_;
  reg                 _zz_22_;
  reg                 _zz_23_;
  reg                 _zz_24_;
  reg        [11:0]   mapper_interrupt_irqHighCtrl_io_masks_driver;
  reg        [11:0]   mapper_interrupt_irqLowCtrl_io_masks_driver;
  reg        [11:0]   mapper_interrupt_irqRiseCtrl_io_masks_driver;
  reg        [11:0]   mapper_interrupt_irqFallCtrl_io_masks_driver;

  assign _zz_39_ = io_bus_PWDATA[0 : 0];
  assign _zz_40_ = io_bus_PWDATA[1 : 1];
  assign _zz_41_ = io_bus_PWDATA[2 : 2];
  assign _zz_42_ = io_bus_PWDATA[3 : 3];
  assign _zz_43_ = io_bus_PWDATA[4 : 4];
  assign _zz_44_ = io_bus_PWDATA[5 : 5];
  assign _zz_45_ = io_bus_PWDATA[6 : 6];
  assign _zz_46_ = io_bus_PWDATA[7 : 7];
  assign _zz_47_ = io_bus_PWDATA[8 : 8];
  assign _zz_48_ = io_bus_PWDATA[9 : 9];
  assign _zz_49_ = io_bus_PWDATA[10 : 10];
  assign _zz_50_ = io_bus_PWDATA[11 : 11];
  assign _zz_51_ = io_bus_PWDATA[0 : 0];
  assign _zz_52_ = io_bus_PWDATA[1 : 1];
  assign _zz_53_ = io_bus_PWDATA[2 : 2];
  assign _zz_54_ = io_bus_PWDATA[3 : 3];
  assign _zz_55_ = io_bus_PWDATA[4 : 4];
  assign _zz_56_ = io_bus_PWDATA[5 : 5];
  assign _zz_57_ = io_bus_PWDATA[6 : 6];
  assign _zz_58_ = io_bus_PWDATA[7 : 7];
  assign _zz_59_ = io_bus_PWDATA[8 : 8];
  assign _zz_60_ = io_bus_PWDATA[9 : 9];
  assign _zz_61_ = io_bus_PWDATA[10 : 10];
  GpioCtrl_1_ ctrl ( 
    .io_gpio_pins_read           (io_gpio_pins_read[11:0]                         ), //i
    .io_gpio_pins_write          (ctrl_io_gpio_pins_write[11:0]                   ), //o
    .io_gpio_pins_writeEnable    (ctrl_io_gpio_pins_writeEnable[11:0]             ), //o
    .io_config_write             (_zz_25_[11:0]                                   ), //i
    .io_config_direction         (_zz_26_[11:0]                                   ), //i
    .io_value                    (ctrl_io_value[11:0]                             ), //o
    .io_enable_high              (_zz_27_[11:0]                                   ), //i
    .io_enable_low               (_zz_28_[11:0]                                   ), //i
    .io_enable_rise              (_zz_29_[11:0]                                   ), //i
    .io_enable_fall              (_zz_30_[11:0]                                   ), //i
    .io_interrupt                (ctrl_io_interrupt[11:0]                         ), //o
    .io_irqHigh_valid            (ctrl_io_irqHigh_valid[11:0]                     ), //o
    .io_irqHigh_pending          (mapper_interrupt_irqHighCtrl_io_pendings[11:0]  ), //i
    .io_irqLow_valid             (ctrl_io_irqLow_valid[11:0]                      ), //o
    .io_irqLow_pending           (mapper_interrupt_irqLowCtrl_io_pendings[11:0]   ), //i
    .io_irqRise_valid            (ctrl_io_irqRise_valid[11:0]                     ), //o
    .io_irqRise_pending          (mapper_interrupt_irqRiseCtrl_io_pendings[11:0]  ), //i
    .io_irqFall_valid            (ctrl_io_irqFall_valid[11:0]                     ), //o
    .io_irqFall_pending          (mapper_interrupt_irqFallCtrl_io_pendings[11:0]  ), //i
    .io_clock                    (io_clock                                        ), //i
    .resetCtrl_systemReset       (resetCtrl_systemReset                           )  //i
  );
  InterruptCtrl_7_ mapper_interrupt_irqHighCtrl ( 
    .io_inputs                (_zz_31_[11:0]                                       ), //i
    .io_clears                (_zz_32_[11:0]                                       ), //i
    .io_masks                 (mapper_interrupt_irqHighCtrl_io_masks_driver[11:0]  ), //i
    .io_pendings              (mapper_interrupt_irqHighCtrl_io_pendings[11:0]      ), //o
    .io_clock                 (io_clock                                            ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                               )  //i
  );
  InterruptCtrl_7_ mapper_interrupt_irqLowCtrl ( 
    .io_inputs                (_zz_33_[11:0]                                      ), //i
    .io_clears                (_zz_34_[11:0]                                      ), //i
    .io_masks                 (mapper_interrupt_irqLowCtrl_io_masks_driver[11:0]  ), //i
    .io_pendings              (mapper_interrupt_irqLowCtrl_io_pendings[11:0]      ), //o
    .io_clock                 (io_clock                                           ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                              )  //i
  );
  InterruptCtrl_7_ mapper_interrupt_irqRiseCtrl ( 
    .io_inputs                (_zz_35_[11:0]                                       ), //i
    .io_clears                (_zz_36_[11:0]                                       ), //i
    .io_masks                 (mapper_interrupt_irqRiseCtrl_io_masks_driver[11:0]  ), //i
    .io_pendings              (mapper_interrupt_irqRiseCtrl_io_pendings[11:0]      ), //o
    .io_clock                 (io_clock                                            ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                               )  //i
  );
  InterruptCtrl_7_ mapper_interrupt_irqFallCtrl ( 
    .io_inputs                (_zz_37_[11:0]                                       ), //i
    .io_clears                (_zz_38_[11:0]                                       ), //i
    .io_masks                 (mapper_interrupt_irqFallCtrl_io_masks_driver[11:0]  ), //i
    .io_pendings              (mapper_interrupt_irqFallCtrl_io_pendings[11:0]      ), //o
    .io_clock                 (io_clock                                            ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                               )  //i
  );
  assign io_gpio_pins_write = ctrl_io_gpio_pins_write;
  assign io_gpio_pins_writeEnable = ctrl_io_gpio_pins_writeEnable;
  assign io_interrupt = ctrl_io_interrupt;
  assign io_bus_PREADY = 1'b1;
  always @ (*) begin
    io_bus_PRDATA = 32'h0;
    case(io_bus_PADDR)
      12'b000000000000 : begin
        io_bus_PRDATA[0 : 0] = ctrl_io_value[0];
        io_bus_PRDATA[1 : 1] = ctrl_io_value[1];
        io_bus_PRDATA[2 : 2] = ctrl_io_value[2];
        io_bus_PRDATA[3 : 3] = ctrl_io_value[3];
        io_bus_PRDATA[4 : 4] = ctrl_io_value[4];
        io_bus_PRDATA[5 : 5] = ctrl_io_value[5];
        io_bus_PRDATA[6 : 6] = ctrl_io_value[6];
        io_bus_PRDATA[7 : 7] = ctrl_io_value[7];
        io_bus_PRDATA[8 : 8] = ctrl_io_value[8];
        io_bus_PRDATA[9 : 9] = ctrl_io_value[9];
        io_bus_PRDATA[10 : 10] = ctrl_io_value[10];
      end
      12'b000000000100 : begin
        io_bus_PRDATA[0 : 0] = _zz_2_;
        io_bus_PRDATA[1 : 1] = _zz_4_;
        io_bus_PRDATA[2 : 2] = _zz_6_;
        io_bus_PRDATA[3 : 3] = _zz_8_;
        io_bus_PRDATA[4 : 4] = _zz_10_;
        io_bus_PRDATA[5 : 5] = _zz_12_;
        io_bus_PRDATA[6 : 6] = _zz_14_;
        io_bus_PRDATA[7 : 7] = _zz_16_;
        io_bus_PRDATA[8 : 8] = _zz_18_;
        io_bus_PRDATA[9 : 9] = _zz_20_;
        io_bus_PRDATA[10 : 10] = _zz_22_;
        io_bus_PRDATA[11 : 11] = _zz_24_;
      end
      12'b000000001000 : begin
        io_bus_PRDATA[0 : 0] = _zz_3_;
        io_bus_PRDATA[1 : 1] = _zz_5_;
        io_bus_PRDATA[2 : 2] = _zz_7_;
        io_bus_PRDATA[3 : 3] = _zz_9_;
        io_bus_PRDATA[4 : 4] = _zz_11_;
        io_bus_PRDATA[5 : 5] = _zz_13_;
        io_bus_PRDATA[6 : 6] = _zz_15_;
        io_bus_PRDATA[7 : 7] = _zz_17_;
        io_bus_PRDATA[8 : 8] = _zz_19_;
        io_bus_PRDATA[9 : 9] = _zz_21_;
        io_bus_PRDATA[10 : 10] = _zz_23_;
        io_bus_PRDATA[11 : 11] = 1'b1;
      end
      12'b000000010000 : begin
        io_bus_PRDATA[11 : 0] = mapper_interrupt_irqHighCtrl_io_pendings;
      end
      12'b000000010100 : begin
        io_bus_PRDATA[11 : 0] = mapper_interrupt_irqHighCtrl_io_masks_driver;
      end
      12'b000000011000 : begin
        io_bus_PRDATA[11 : 0] = mapper_interrupt_irqLowCtrl_io_pendings;
      end
      12'b000000011100 : begin
        io_bus_PRDATA[11 : 0] = mapper_interrupt_irqLowCtrl_io_masks_driver;
      end
      12'b000000100000 : begin
        io_bus_PRDATA[11 : 0] = mapper_interrupt_irqRiseCtrl_io_pendings;
      end
      12'b000000100100 : begin
        io_bus_PRDATA[11 : 0] = mapper_interrupt_irqRiseCtrl_io_masks_driver;
      end
      12'b000000101000 : begin
        io_bus_PRDATA[11 : 0] = mapper_interrupt_irqFallCtrl_io_pendings;
      end
      12'b000000101100 : begin
        io_bus_PRDATA[11 : 0] = mapper_interrupt_irqFallCtrl_io_masks_driver;
      end
      default : begin
      end
    endcase
  end

  assign io_bus_PSLVERROR = 1'b0;
  assign _zz_1_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && io_bus_PWRITE);
  always @ (*) begin
    _zz_25_[0] = _zz_2_;
    _zz_25_[1] = _zz_4_;
    _zz_25_[2] = _zz_6_;
    _zz_25_[3] = _zz_8_;
    _zz_25_[4] = _zz_10_;
    _zz_25_[5] = _zz_12_;
    _zz_25_[6] = _zz_14_;
    _zz_25_[7] = _zz_16_;
    _zz_25_[8] = _zz_18_;
    _zz_25_[9] = _zz_20_;
    _zz_25_[10] = _zz_22_;
    _zz_25_[11] = _zz_24_;
  end

  always @ (*) begin
    _zz_26_[0] = _zz_3_;
    _zz_26_[1] = _zz_5_;
    _zz_26_[2] = _zz_7_;
    _zz_26_[3] = _zz_9_;
    _zz_26_[4] = _zz_11_;
    _zz_26_[5] = _zz_13_;
    _zz_26_[6] = _zz_15_;
    _zz_26_[7] = _zz_17_;
    _zz_26_[8] = _zz_19_;
    _zz_26_[9] = _zz_21_;
    _zz_26_[10] = _zz_23_;
    _zz_26_[11] = 1'b1;
  end

  always @ (*) begin
    _zz_32_ = 12'h0;
    case(io_bus_PADDR)
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
      end
      12'b000000010000 : begin
        if(_zz_1_)begin
          _zz_32_ = io_bus_PWDATA[11 : 0];
        end
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
      end
      12'b000000100100 : begin
      end
      12'b000000101000 : begin
      end
      12'b000000101100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_34_ = 12'h0;
    case(io_bus_PADDR)
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
        if(_zz_1_)begin
          _zz_34_ = io_bus_PWDATA[11 : 0];
        end
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
      end
      12'b000000100100 : begin
      end
      12'b000000101000 : begin
      end
      12'b000000101100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_36_ = 12'h0;
    case(io_bus_PADDR)
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
        if(_zz_1_)begin
          _zz_36_ = io_bus_PWDATA[11 : 0];
        end
      end
      12'b000000100100 : begin
      end
      12'b000000101000 : begin
      end
      12'b000000101100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_38_ = 12'h0;
    case(io_bus_PADDR)
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000001000 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
      end
      12'b000000100100 : begin
      end
      12'b000000101000 : begin
        if(_zz_1_)begin
          _zz_38_ = io_bus_PWDATA[11 : 0];
        end
      end
      12'b000000101100 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_31_[0] = ctrl_io_irqHigh_valid[0];
    _zz_31_[1] = ctrl_io_irqHigh_valid[1];
    _zz_31_[2] = ctrl_io_irqHigh_valid[2];
    _zz_31_[3] = ctrl_io_irqHigh_valid[3];
    _zz_31_[4] = ctrl_io_irqHigh_valid[4];
    _zz_31_[5] = ctrl_io_irqHigh_valid[5];
    _zz_31_[6] = ctrl_io_irqHigh_valid[6];
    _zz_31_[7] = ctrl_io_irqHigh_valid[7];
    _zz_31_[8] = ctrl_io_irqHigh_valid[8];
    _zz_31_[9] = ctrl_io_irqHigh_valid[9];
    _zz_31_[10] = ctrl_io_irqHigh_valid[10];
    _zz_31_[11] = 1'b0;
  end

  always @ (*) begin
    _zz_33_[0] = ctrl_io_irqLow_valid[0];
    _zz_33_[1] = ctrl_io_irqLow_valid[1];
    _zz_33_[2] = ctrl_io_irqLow_valid[2];
    _zz_33_[3] = ctrl_io_irqLow_valid[3];
    _zz_33_[4] = ctrl_io_irqLow_valid[4];
    _zz_33_[5] = ctrl_io_irqLow_valid[5];
    _zz_33_[6] = ctrl_io_irqLow_valid[6];
    _zz_33_[7] = ctrl_io_irqLow_valid[7];
    _zz_33_[8] = ctrl_io_irqLow_valid[8];
    _zz_33_[9] = ctrl_io_irqLow_valid[9];
    _zz_33_[10] = ctrl_io_irqLow_valid[10];
    _zz_33_[11] = 1'b0;
  end

  always @ (*) begin
    _zz_35_[0] = ctrl_io_irqRise_valid[0];
    _zz_35_[1] = ctrl_io_irqRise_valid[1];
    _zz_35_[2] = ctrl_io_irqRise_valid[2];
    _zz_35_[3] = ctrl_io_irqRise_valid[3];
    _zz_35_[4] = ctrl_io_irqRise_valid[4];
    _zz_35_[5] = ctrl_io_irqRise_valid[5];
    _zz_35_[6] = ctrl_io_irqRise_valid[6];
    _zz_35_[7] = ctrl_io_irqRise_valid[7];
    _zz_35_[8] = ctrl_io_irqRise_valid[8];
    _zz_35_[9] = ctrl_io_irqRise_valid[9];
    _zz_35_[10] = ctrl_io_irqRise_valid[10];
    _zz_35_[11] = 1'b0;
  end

  always @ (*) begin
    _zz_37_[0] = ctrl_io_irqFall_valid[0];
    _zz_37_[1] = ctrl_io_irqFall_valid[1];
    _zz_37_[2] = ctrl_io_irqFall_valid[2];
    _zz_37_[3] = ctrl_io_irqFall_valid[3];
    _zz_37_[4] = ctrl_io_irqFall_valid[4];
    _zz_37_[5] = ctrl_io_irqFall_valid[5];
    _zz_37_[6] = ctrl_io_irqFall_valid[6];
    _zz_37_[7] = ctrl_io_irqFall_valid[7];
    _zz_37_[8] = ctrl_io_irqFall_valid[8];
    _zz_37_[9] = ctrl_io_irqFall_valid[9];
    _zz_37_[10] = ctrl_io_irqFall_valid[10];
    _zz_37_[11] = 1'b0;
  end

  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      _zz_2_ <= 1'b0;
      _zz_3_ <= 1'b0;
      _zz_4_ <= 1'b0;
      _zz_5_ <= 1'b0;
      _zz_6_ <= 1'b0;
      _zz_7_ <= 1'b0;
      _zz_8_ <= 1'b0;
      _zz_9_ <= 1'b0;
      _zz_10_ <= 1'b0;
      _zz_11_ <= 1'b0;
      _zz_12_ <= 1'b0;
      _zz_13_ <= 1'b0;
      _zz_14_ <= 1'b0;
      _zz_15_ <= 1'b0;
      _zz_16_ <= 1'b0;
      _zz_17_ <= 1'b0;
      _zz_18_ <= 1'b0;
      _zz_19_ <= 1'b0;
      _zz_20_ <= 1'b0;
      _zz_21_ <= 1'b0;
      _zz_22_ <= 1'b0;
      _zz_23_ <= 1'b0;
      _zz_24_ <= 1'b0;
      mapper_interrupt_irqHighCtrl_io_masks_driver <= 12'h0;
      mapper_interrupt_irqLowCtrl_io_masks_driver <= 12'h0;
      mapper_interrupt_irqRiseCtrl_io_masks_driver <= 12'h0;
      mapper_interrupt_irqFallCtrl_io_masks_driver <= 12'h0;
    end else begin
      case(io_bus_PADDR)
        12'b000000000000 : begin
        end
        12'b000000000100 : begin
          if(_zz_1_)begin
            _zz_2_ <= _zz_39_[0];
            _zz_4_ <= _zz_40_[0];
            _zz_6_ <= _zz_41_[0];
            _zz_8_ <= _zz_42_[0];
            _zz_10_ <= _zz_43_[0];
            _zz_12_ <= _zz_44_[0];
            _zz_14_ <= _zz_45_[0];
            _zz_16_ <= _zz_46_[0];
            _zz_18_ <= _zz_47_[0];
            _zz_20_ <= _zz_48_[0];
            _zz_22_ <= _zz_49_[0];
            _zz_24_ <= _zz_50_[0];
          end
        end
        12'b000000001000 : begin
          if(_zz_1_)begin
            _zz_3_ <= _zz_51_[0];
            _zz_5_ <= _zz_52_[0];
            _zz_7_ <= _zz_53_[0];
            _zz_9_ <= _zz_54_[0];
            _zz_11_ <= _zz_55_[0];
            _zz_13_ <= _zz_56_[0];
            _zz_15_ <= _zz_57_[0];
            _zz_17_ <= _zz_58_[0];
            _zz_19_ <= _zz_59_[0];
            _zz_21_ <= _zz_60_[0];
            _zz_23_ <= _zz_61_[0];
          end
        end
        12'b000000010000 : begin
        end
        12'b000000010100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqHighCtrl_io_masks_driver <= io_bus_PWDATA[11 : 0];
          end
        end
        12'b000000011000 : begin
        end
        12'b000000011100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqLowCtrl_io_masks_driver <= io_bus_PWDATA[11 : 0];
          end
        end
        12'b000000100000 : begin
        end
        12'b000000100100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqRiseCtrl_io_masks_driver <= io_bus_PWDATA[11 : 0];
          end
        end
        12'b000000101000 : begin
        end
        12'b000000101100 : begin
          if(_zz_1_)begin
            mapper_interrupt_irqFallCtrl_io_masks_driver <= io_bus_PWDATA[11 : 0];
          end
        end
        default : begin
        end
      endcase
    end
  end


endmodule

module Apb3SpiMaster (
  input      [11:0]   io_bus_PADDR,
  input      [0:0]    io_bus_PSEL,
  input               io_bus_PENABLE,
  output              io_bus_PREADY,
  input               io_bus_PWRITE,
  input      [31:0]   io_bus_PWDATA,
  output reg [31:0]   io_bus_PRDATA,
  output              io_bus_PSLVERROR,
  output     [0:0]    io_spi_ss,
  output              io_spi_sclk,
  output              io_spi_mosi,
  input               io_spi_miso,
  output              io_interrupt,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire                _zz_10_;
  reg                 _zz_11_;
  wire                _zz_12_;
  reg        [1:0]    _zz_13_;
  reg        [1:0]    _zz_14_;
  wire       [0:0]    spiMasterCtrl_1__io_spi_ss;
  wire                spiMasterCtrl_1__io_spi_sclk;
  wire                spiMasterCtrl_1__io_spi_mosi;
  wire                spiMasterCtrl_1__io_interrupt;
  wire                spiMasterCtrl_1__io_cmd_ready;
  wire                spiMasterCtrl_1__io_rsp_valid;
  wire       [7:0]    spiMasterCtrl_1__io_rsp_payload;
  wire                mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_push_ready;
  wire                mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_valid;
  wire       `CmdMode_defaultEncoding_type mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_payload_mode;
  wire       [8:0]    mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_payload_args;
  wire       [4:0]    mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_occupancy;
  wire       [4:0]    mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_availability;
  wire                spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_push_ready;
  wire                spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_pop_valid;
  wire       [7:0]    spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_pop_payload;
  wire       [4:0]    spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_occupancy;
  wire       [4:0]    spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_availability;
  wire       [1:0]    mapper_interruptCtrl_irqCtrl_io_pendings;
  wire       [0:0]    _zz_15_;
  wire       [0:0]    _zz_16_;
  wire       [0:0]    _zz_17_;
  wire       [0:0]    _zz_18_;
  wire       [0:0]    _zz_19_;
  wire                _zz_1_;
  wire                _zz_2_;
  reg        [15:0]   mapper_config_cfg_clockDivider;
  reg        [0:0]    mapper_config_cfg_ss_activeHigh;
  reg        [15:0]   mapper_config_cfg_ss_setup;
  reg        [15:0]   mapper_config_cfg_ss_hold;
  reg        [15:0]   mapper_config_cfg_ss_disable;
  reg                 mapper_config_modeCfg_cpol;
  reg                 mapper_config_modeCfg_cpha;
  reg        [15:0]   _zz_3_;
  reg        [15:0]   _zz_4_;
  reg        [15:0]   _zz_5_;
  reg        [15:0]   _zz_6_;
  wire                mapper_cmdLogic_streamUnbuffered_valid;
  wire                mapper_cmdLogic_streamUnbuffered_ready;
  wire       `CmdMode_defaultEncoding_type mapper_cmdLogic_streamUnbuffered_payload_mode;
  reg        [8:0]    mapper_cmdLogic_streamUnbuffered_payload_args;
  reg                 _zz_7_;
  wire       [7:0]    mapper_cmdLogic_dataCmd_data;
  wire                mapper_cmdLogic_dataCmd_read;
  wire                mapper_cmdLogic_ssCmd_enable;
  reg        [1:0]    mapper_interruptCtrl_irqCtrl_io_masks_driver;
  wire       `CmdMode_defaultEncoding_type _zz_8_;
  wire       [1:0]    _zz_9_;
  `ifndef SYNTHESIS
  reg [31:0] mapper_cmdLogic_streamUnbuffered_payload_mode_string;
  reg [31:0] _zz_8__string;
  `endif


  assign _zz_15_ = mapper_cmdLogic_ssCmd_enable;
  assign _zz_16_ = io_bus_PWDATA[24 : 24];
  assign _zz_17_ = io_bus_PWDATA[24 : 24];
  assign _zz_18_ = _zz_9_[0 : 0];
  assign _zz_19_ = _zz_9_[1 : 1];
  SpiMasterCtrl spiMasterCtrl_1_ ( 
    .io_config_clockDivider     (mapper_config_cfg_clockDivider[15:0]                                             ), //i
    .io_config_ss_activeHigh    (mapper_config_cfg_ss_activeHigh                                                  ), //i
    .io_config_ss_setup         (mapper_config_cfg_ss_setup[15:0]                                                 ), //i
    .io_config_ss_hold          (mapper_config_cfg_ss_hold[15:0]                                                  ), //i
    .io_config_ss_disable       (mapper_config_cfg_ss_disable[15:0]                                               ), //i
    .io_modeConfig_cpol         (mapper_config_modeCfg_cpol                                                       ), //i
    .io_modeConfig_cpha         (mapper_config_modeCfg_cpha                                                       ), //i
    .io_spi_ss                  (spiMasterCtrl_1__io_spi_ss                                                       ), //o
    .io_spi_sclk                (spiMasterCtrl_1__io_spi_sclk                                                     ), //o
    .io_spi_mosi                (spiMasterCtrl_1__io_spi_mosi                                                     ), //o
    .io_spi_miso                (io_spi_miso                                                                      ), //i
    .io_interrupt               (spiMasterCtrl_1__io_interrupt                                                    ), //o
    .io_pendingInterrupts       (mapper_interruptCtrl_irqCtrl_io_pendings[1:0]                                    ), //i
    .io_cmd_valid               (mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_valid              ), //i
    .io_cmd_ready               (spiMasterCtrl_1__io_cmd_ready                                                    ), //o
    .io_cmd_payload_mode        (mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_payload_mode       ), //i
    .io_cmd_payload_args        (mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_payload_args[8:0]  ), //i
    .io_rsp_valid               (spiMasterCtrl_1__io_rsp_valid                                                    ), //o
    .io_rsp_payload             (spiMasterCtrl_1__io_rsp_payload[7:0]                                             ), //o
    .io_clock                   (io_clock                                                                         ), //i
    .resetCtrl_systemReset      (resetCtrl_systemReset                                                            )  //i
  );
  StreamFifo_6_ mapper_cmdLogic_streamUnbuffered_queueWithAvailability ( 
    .io_push_valid            (mapper_cmdLogic_streamUnbuffered_valid                                           ), //i
    .io_push_ready            (mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_push_ready             ), //o
    .io_push_payload_mode     (mapper_cmdLogic_streamUnbuffered_payload_mode                                    ), //i
    .io_push_payload_args     (mapper_cmdLogic_streamUnbuffered_payload_args[8:0]                               ), //i
    .io_pop_valid             (mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_valid              ), //o
    .io_pop_ready             (spiMasterCtrl_1__io_cmd_ready                                                    ), //i
    .io_pop_payload_mode      (mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_payload_mode       ), //o
    .io_pop_payload_args      (mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_payload_args[8:0]  ), //o
    .io_flush                 (_zz_10_                                                                          ), //i
    .io_occupancy             (mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_occupancy[4:0]         ), //o
    .io_availability          (mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_availability[4:0]      ), //o
    .io_clock                 (io_clock                                                                         ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                                                            )  //i
  );
  StreamFifo_7_ spiMasterCtrl_1__io_rsp_queueWithOccupancy ( 
    .io_push_valid            (spiMasterCtrl_1__io_rsp_valid                                    ), //i
    .io_push_ready            (spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_push_ready         ), //o
    .io_push_payload          (spiMasterCtrl_1__io_rsp_payload[7:0]                             ), //i
    .io_pop_valid             (spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_pop_valid          ), //o
    .io_pop_ready             (_zz_11_                                                          ), //i
    .io_pop_payload           (spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_pop_payload[7:0]   ), //o
    .io_flush                 (_zz_12_                                                          ), //i
    .io_occupancy             (spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_occupancy[4:0]     ), //o
    .io_availability          (spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_availability[4:0]  ), //o
    .io_clock                 (io_clock                                                         ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                                            )  //i
  );
  InterruptCtrl mapper_interruptCtrl_irqCtrl ( 
    .io_inputs                (_zz_13_[1:0]                                       ), //i
    .io_clears                (_zz_14_[1:0]                                       ), //i
    .io_masks                 (mapper_interruptCtrl_irqCtrl_io_masks_driver[1:0]  ), //i
    .io_pendings              (mapper_interruptCtrl_irqCtrl_io_pendings[1:0]      ), //o
    .io_clock                 (io_clock                                           ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                              )  //i
  );
  `ifndef SYNTHESIS
  always @(*) begin
    case(mapper_cmdLogic_streamUnbuffered_payload_mode)
      `CmdMode_defaultEncoding_DATA : mapper_cmdLogic_streamUnbuffered_payload_mode_string = "DATA";
      `CmdMode_defaultEncoding_SS : mapper_cmdLogic_streamUnbuffered_payload_mode_string = "SS  ";
      default : mapper_cmdLogic_streamUnbuffered_payload_mode_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_8_)
      `CmdMode_defaultEncoding_DATA : _zz_8__string = "DATA";
      `CmdMode_defaultEncoding_SS : _zz_8__string = "SS  ";
      default : _zz_8__string = "????";
    endcase
  end
  `endif

  assign io_spi_ss = spiMasterCtrl_1__io_spi_ss;
  assign io_spi_sclk = spiMasterCtrl_1__io_spi_sclk;
  assign io_spi_mosi = spiMasterCtrl_1__io_spi_mosi;
  assign io_interrupt = spiMasterCtrl_1__io_interrupt;
  assign io_bus_PREADY = 1'b1;
  always @ (*) begin
    io_bus_PRDATA = 32'h0;
    case(io_bus_PADDR)
      12'b000000001000 : begin
        io_bus_PRDATA[1 : 0] = {mapper_config_modeCfg_cpha,mapper_config_modeCfg_cpol};
        io_bus_PRDATA[4 : 4] = mapper_config_cfg_ss_activeHigh;
      end
      12'b000000001100 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000000000 : begin
        io_bus_PRDATA[31 : 31] = (spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_pop_valid ^ 1'b0);
        io_bus_PRDATA[7 : 0] = spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_pop_payload;
      end
      12'b000000000100 : begin
        io_bus_PRDATA[20 : 16] = mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_availability;
        io_bus_PRDATA[4 : 0] = spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_occupancy;
      end
      12'b000000011100 : begin
        io_bus_PRDATA[1 : 0] = mapper_interruptCtrl_irqCtrl_io_pendings;
      end
      12'b000000100000 : begin
        io_bus_PRDATA[1 : 0] = mapper_interruptCtrl_irqCtrl_io_masks_driver;
      end
      default : begin
      end
    endcase
  end

  assign io_bus_PSLVERROR = 1'b0;
  assign _zz_1_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && io_bus_PWRITE);
  assign _zz_2_ = (((io_bus_PSEL[0] && io_bus_PENABLE) && io_bus_PREADY) && (! io_bus_PWRITE));
  always @ (*) begin
    _zz_7_ = 1'b0;
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000000000 : begin
        if(_zz_1_)begin
          _zz_7_ = 1'b1;
        end
      end
      12'b000000000100 : begin
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
      end
      default : begin
      end
    endcase
  end

  assign mapper_cmdLogic_streamUnbuffered_valid = _zz_7_;
  always @ (*) begin
    case(mapper_cmdLogic_streamUnbuffered_payload_mode)
      `CmdMode_defaultEncoding_DATA : begin
        mapper_cmdLogic_streamUnbuffered_payload_args = {mapper_cmdLogic_dataCmd_read,mapper_cmdLogic_dataCmd_data};
      end
      default : begin
        mapper_cmdLogic_streamUnbuffered_payload_args = {8'd0, _zz_15_};
      end
    endcase
  end

  assign mapper_cmdLogic_streamUnbuffered_ready = mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_push_ready;
  always @ (*) begin
    _zz_11_ = 1'b0;
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000000000 : begin
        if(_zz_2_)begin
          _zz_11_ = 1'b1;
        end
      end
      12'b000000000100 : begin
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_14_ = (2'b00);
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
      end
      12'b000000010000 : begin
      end
      12'b000000010100 : begin
      end
      12'b000000011000 : begin
      end
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000011100 : begin
        if(_zz_1_)begin
          _zz_14_ = io_bus_PWDATA[1 : 0];
        end
      end
      12'b000000100000 : begin
      end
      default : begin
      end
    endcase
  end

  always @ (*) begin
    _zz_13_[0] = (! mapper_cmdLogic_streamUnbuffered_queueWithAvailability_io_pop_valid);
    _zz_13_[1] = spiMasterCtrl_1__io_rsp_queueWithOccupancy_io_pop_valid;
  end

  assign mapper_cmdLogic_dataCmd_data = io_bus_PWDATA[7 : 0];
  assign mapper_cmdLogic_dataCmd_read = _zz_16_[0];
  assign mapper_cmdLogic_ssCmd_enable = _zz_17_[0];
  assign _zz_8_ = io_bus_PWDATA[28 : 28];
  assign mapper_cmdLogic_streamUnbuffered_payload_mode = _zz_8_;
  assign _zz_9_ = io_bus_PWDATA[1 : 0];
  assign _zz_10_ = 1'b0;
  assign _zz_12_ = 1'b0;
  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      mapper_config_cfg_ss_activeHigh <= (1'b0);
      mapper_config_modeCfg_cpol <= 1'b0;
      mapper_config_modeCfg_cpha <= 1'b0;
      mapper_interruptCtrl_irqCtrl_io_masks_driver <= (2'b00);
    end else begin
      case(io_bus_PADDR)
        12'b000000001000 : begin
          if(_zz_1_)begin
            mapper_config_modeCfg_cpol <= _zz_18_[0];
            mapper_config_modeCfg_cpha <= _zz_19_[0];
            mapper_config_cfg_ss_activeHigh <= io_bus_PWDATA[4 : 4];
          end
        end
        12'b000000001100 : begin
        end
        12'b000000010000 : begin
        end
        12'b000000010100 : begin
        end
        12'b000000011000 : begin
        end
        12'b000000000000 : begin
        end
        12'b000000000100 : begin
        end
        12'b000000011100 : begin
        end
        12'b000000100000 : begin
          if(_zz_1_)begin
            mapper_interruptCtrl_irqCtrl_io_masks_driver <= io_bus_PWDATA[1 : 0];
          end
        end
        default : begin
        end
      endcase
    end
  end

  always @ (posedge io_clock) begin
    mapper_config_cfg_clockDivider <= _zz_3_;
    mapper_config_cfg_ss_setup <= _zz_4_;
    mapper_config_cfg_ss_hold <= _zz_5_;
    mapper_config_cfg_ss_disable <= _zz_6_;
    case(io_bus_PADDR)
      12'b000000001000 : begin
      end
      12'b000000001100 : begin
        if(_zz_1_)begin
          _zz_3_ <= io_bus_PWDATA[15 : 0];
        end
      end
      12'b000000010000 : begin
        if(_zz_1_)begin
          _zz_4_ <= io_bus_PWDATA[15 : 0];
        end
      end
      12'b000000010100 : begin
        if(_zz_1_)begin
          _zz_5_ <= io_bus_PWDATA[15 : 0];
        end
      end
      12'b000000011000 : begin
        if(_zz_1_)begin
          _zz_6_ <= io_bus_PWDATA[15 : 0];
        end
      end
      12'b000000000000 : begin
      end
      12'b000000000100 : begin
      end
      12'b000000011100 : begin
      end
      12'b000000100000 : begin
      end
      default : begin
      end
    endcase
  end


endmodule

module Apb3UniqueID (
  input      [11:0]   io_bus_PADDR,
  input      [0:0]    io_bus_PSEL,
  input               io_bus_PENABLE,
  output              io_bus_PREADY,
  input               io_bus_PWRITE,
  input      [31:0]   io_bus_PWDATA,
  output reg [31:0]   io_bus_PRDATA,
  output              io_bus_PSLVERROR,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  wire       [31:0]   mapper_id;

  UniqueIDCtrl ctrl ( 
  );
  assign io_bus_PREADY = 1'b1;
  always @ (*) begin
    io_bus_PRDATA = 32'h0;
    case(io_bus_PADDR)
      12'b000000000000 : begin
        io_bus_PRDATA[31 : 0] = mapper_id;
      end
      default : begin
      end
    endcase
  end

  assign io_bus_PSLVERROR = 1'b0;
  assign mapper_id = 32'hcafebabe;

endmodule

module Apb3Decoder (
  input      [19:0]   io_input_PADDR,
  input      [0:0]    io_input_PSEL,
  input               io_input_PENABLE,
  output reg          io_input_PREADY,
  input               io_input_PWRITE,
  input      [31:0]   io_input_PWDATA,
  output     [31:0]   io_input_PRDATA,
  output reg          io_input_PSLVERROR,
  output     [19:0]   io_output_PADDR,
  output reg [8:0]    io_output_PSEL,
  output              io_output_PENABLE,
  input               io_output_PREADY,
  output              io_output_PWRITE,
  output     [31:0]   io_output_PWDATA,
  input      [31:0]   io_output_PRDATA,
  input               io_output_PSLVERROR 
);
  wire                _zz_1_;

  assign _zz_1_ = (io_input_PSEL[0] && (io_output_PSEL == 9'h0));
  assign io_output_PADDR = io_input_PADDR;
  assign io_output_PENABLE = io_input_PENABLE;
  assign io_output_PWRITE = io_input_PWRITE;
  assign io_output_PWDATA = io_input_PWDATA;
  always @ (*) begin
    io_output_PSEL[0] = (((io_input_PADDR & (~ 20'h00fff)) == 20'h20000) && io_input_PSEL[0]);
    io_output_PSEL[1] = (((io_input_PADDR & (~ 20'h0ffff)) == 20'hf0000) && io_input_PSEL[0]);
    io_output_PSEL[2] = (((io_input_PADDR & (~ 20'h00fff)) == 20'h0) && io_input_PSEL[0]);
    io_output_PSEL[3] = (((io_input_PADDR & (~ 20'h00fff)) == 20'h01000) && io_input_PSEL[0]);
    io_output_PSEL[4] = (((io_input_PADDR & (~ 20'h00fff)) == 20'h02000) && io_input_PSEL[0]);
    io_output_PSEL[5] = (((io_input_PADDR & (~ 20'h00fff)) == 20'h10000) && io_input_PSEL[0]);
    io_output_PSEL[6] = (((io_input_PADDR & (~ 20'h00fff)) == 20'h11000) && io_input_PSEL[0]);
    io_output_PSEL[7] = (((io_input_PADDR & (~ 20'h00fff)) == 20'h40000) && io_input_PSEL[0]);
    io_output_PSEL[8] = (((io_input_PADDR & (~ 20'h00fff)) == 20'ha0000) && io_input_PSEL[0]);
  end

  always @ (*) begin
    io_input_PREADY = io_output_PREADY;
    if(_zz_1_)begin
      io_input_PREADY = 1'b1;
    end
  end

  assign io_input_PRDATA = io_output_PRDATA;
  always @ (*) begin
    io_input_PSLVERROR = io_output_PSLVERROR;
    if(_zz_1_)begin
      io_input_PSLVERROR = 1'b1;
    end
  end


endmodule

module Apb3Router (
  input      [19:0]   io_input_PADDR,
  input      [8:0]    io_input_PSEL,
  input               io_input_PENABLE,
  output              io_input_PREADY,
  input               io_input_PWRITE,
  input      [31:0]   io_input_PWDATA,
  output     [31:0]   io_input_PRDATA,
  output              io_input_PSLVERROR,
  output     [19:0]   io_outputs_0_PADDR,
  output     [0:0]    io_outputs_0_PSEL,
  output              io_outputs_0_PENABLE,
  input               io_outputs_0_PREADY,
  output              io_outputs_0_PWRITE,
  output     [31:0]   io_outputs_0_PWDATA,
  input      [31:0]   io_outputs_0_PRDATA,
  input               io_outputs_0_PSLVERROR,
  output     [19:0]   io_outputs_1_PADDR,
  output     [0:0]    io_outputs_1_PSEL,
  output              io_outputs_1_PENABLE,
  input               io_outputs_1_PREADY,
  output              io_outputs_1_PWRITE,
  output     [31:0]   io_outputs_1_PWDATA,
  input      [31:0]   io_outputs_1_PRDATA,
  input               io_outputs_1_PSLVERROR,
  output     [19:0]   io_outputs_2_PADDR,
  output     [0:0]    io_outputs_2_PSEL,
  output              io_outputs_2_PENABLE,
  input               io_outputs_2_PREADY,
  output              io_outputs_2_PWRITE,
  output     [31:0]   io_outputs_2_PWDATA,
  input      [31:0]   io_outputs_2_PRDATA,
  input               io_outputs_2_PSLVERROR,
  output     [19:0]   io_outputs_3_PADDR,
  output     [0:0]    io_outputs_3_PSEL,
  output              io_outputs_3_PENABLE,
  input               io_outputs_3_PREADY,
  output              io_outputs_3_PWRITE,
  output     [31:0]   io_outputs_3_PWDATA,
  input      [31:0]   io_outputs_3_PRDATA,
  input               io_outputs_3_PSLVERROR,
  output     [19:0]   io_outputs_4_PADDR,
  output     [0:0]    io_outputs_4_PSEL,
  output              io_outputs_4_PENABLE,
  input               io_outputs_4_PREADY,
  output              io_outputs_4_PWRITE,
  output     [31:0]   io_outputs_4_PWDATA,
  input      [31:0]   io_outputs_4_PRDATA,
  input               io_outputs_4_PSLVERROR,
  output     [19:0]   io_outputs_5_PADDR,
  output     [0:0]    io_outputs_5_PSEL,
  output              io_outputs_5_PENABLE,
  input               io_outputs_5_PREADY,
  output              io_outputs_5_PWRITE,
  output     [31:0]   io_outputs_5_PWDATA,
  input      [31:0]   io_outputs_5_PRDATA,
  input               io_outputs_5_PSLVERROR,
  output     [19:0]   io_outputs_6_PADDR,
  output     [0:0]    io_outputs_6_PSEL,
  output              io_outputs_6_PENABLE,
  input               io_outputs_6_PREADY,
  output              io_outputs_6_PWRITE,
  output     [31:0]   io_outputs_6_PWDATA,
  input      [31:0]   io_outputs_6_PRDATA,
  input               io_outputs_6_PSLVERROR,
  output     [19:0]   io_outputs_7_PADDR,
  output     [0:0]    io_outputs_7_PSEL,
  output              io_outputs_7_PENABLE,
  input               io_outputs_7_PREADY,
  output              io_outputs_7_PWRITE,
  output     [31:0]   io_outputs_7_PWDATA,
  input      [31:0]   io_outputs_7_PRDATA,
  input               io_outputs_7_PSLVERROR,
  output     [19:0]   io_outputs_8_PADDR,
  output     [0:0]    io_outputs_8_PSEL,
  output              io_outputs_8_PENABLE,
  input               io_outputs_8_PREADY,
  output              io_outputs_8_PWRITE,
  output     [31:0]   io_outputs_8_PWDATA,
  input      [31:0]   io_outputs_8_PRDATA,
  input               io_outputs_8_PSLVERROR,
  input               io_clock,
  input               resetCtrl_systemReset 
);
  reg                 _zz_9_;
  reg        [31:0]   _zz_10_;
  reg                 _zz_11_;
  wire                _zz_1_;
  wire                _zz_2_;
  wire                _zz_3_;
  wire                _zz_4_;
  wire                _zz_5_;
  wire                _zz_6_;
  wire                _zz_7_;
  wire                _zz_8_;
  reg        [3:0]    selIndex;

  always @(*) begin
    case(selIndex)
      4'b0000 : begin
        _zz_9_ = io_outputs_0_PREADY;
        _zz_10_ = io_outputs_0_PRDATA;
        _zz_11_ = io_outputs_0_PSLVERROR;
      end
      4'b0001 : begin
        _zz_9_ = io_outputs_1_PREADY;
        _zz_10_ = io_outputs_1_PRDATA;
        _zz_11_ = io_outputs_1_PSLVERROR;
      end
      4'b0010 : begin
        _zz_9_ = io_outputs_2_PREADY;
        _zz_10_ = io_outputs_2_PRDATA;
        _zz_11_ = io_outputs_2_PSLVERROR;
      end
      4'b0011 : begin
        _zz_9_ = io_outputs_3_PREADY;
        _zz_10_ = io_outputs_3_PRDATA;
        _zz_11_ = io_outputs_3_PSLVERROR;
      end
      4'b0100 : begin
        _zz_9_ = io_outputs_4_PREADY;
        _zz_10_ = io_outputs_4_PRDATA;
        _zz_11_ = io_outputs_4_PSLVERROR;
      end
      4'b0101 : begin
        _zz_9_ = io_outputs_5_PREADY;
        _zz_10_ = io_outputs_5_PRDATA;
        _zz_11_ = io_outputs_5_PSLVERROR;
      end
      4'b0110 : begin
        _zz_9_ = io_outputs_6_PREADY;
        _zz_10_ = io_outputs_6_PRDATA;
        _zz_11_ = io_outputs_6_PSLVERROR;
      end
      4'b0111 : begin
        _zz_9_ = io_outputs_7_PREADY;
        _zz_10_ = io_outputs_7_PRDATA;
        _zz_11_ = io_outputs_7_PSLVERROR;
      end
      default : begin
        _zz_9_ = io_outputs_8_PREADY;
        _zz_10_ = io_outputs_8_PRDATA;
        _zz_11_ = io_outputs_8_PSLVERROR;
      end
    endcase
  end

  assign io_outputs_0_PADDR = io_input_PADDR;
  assign io_outputs_0_PENABLE = io_input_PENABLE;
  assign io_outputs_0_PSEL[0] = io_input_PSEL[0];
  assign io_outputs_0_PWRITE = io_input_PWRITE;
  assign io_outputs_0_PWDATA = io_input_PWDATA;
  assign io_outputs_1_PADDR = io_input_PADDR;
  assign io_outputs_1_PENABLE = io_input_PENABLE;
  assign io_outputs_1_PSEL[0] = io_input_PSEL[1];
  assign io_outputs_1_PWRITE = io_input_PWRITE;
  assign io_outputs_1_PWDATA = io_input_PWDATA;
  assign io_outputs_2_PADDR = io_input_PADDR;
  assign io_outputs_2_PENABLE = io_input_PENABLE;
  assign io_outputs_2_PSEL[0] = io_input_PSEL[2];
  assign io_outputs_2_PWRITE = io_input_PWRITE;
  assign io_outputs_2_PWDATA = io_input_PWDATA;
  assign io_outputs_3_PADDR = io_input_PADDR;
  assign io_outputs_3_PENABLE = io_input_PENABLE;
  assign io_outputs_3_PSEL[0] = io_input_PSEL[3];
  assign io_outputs_3_PWRITE = io_input_PWRITE;
  assign io_outputs_3_PWDATA = io_input_PWDATA;
  assign io_outputs_4_PADDR = io_input_PADDR;
  assign io_outputs_4_PENABLE = io_input_PENABLE;
  assign io_outputs_4_PSEL[0] = io_input_PSEL[4];
  assign io_outputs_4_PWRITE = io_input_PWRITE;
  assign io_outputs_4_PWDATA = io_input_PWDATA;
  assign io_outputs_5_PADDR = io_input_PADDR;
  assign io_outputs_5_PENABLE = io_input_PENABLE;
  assign io_outputs_5_PSEL[0] = io_input_PSEL[5];
  assign io_outputs_5_PWRITE = io_input_PWRITE;
  assign io_outputs_5_PWDATA = io_input_PWDATA;
  assign io_outputs_6_PADDR = io_input_PADDR;
  assign io_outputs_6_PENABLE = io_input_PENABLE;
  assign io_outputs_6_PSEL[0] = io_input_PSEL[6];
  assign io_outputs_6_PWRITE = io_input_PWRITE;
  assign io_outputs_6_PWDATA = io_input_PWDATA;
  assign io_outputs_7_PADDR = io_input_PADDR;
  assign io_outputs_7_PENABLE = io_input_PENABLE;
  assign io_outputs_7_PSEL[0] = io_input_PSEL[7];
  assign io_outputs_7_PWRITE = io_input_PWRITE;
  assign io_outputs_7_PWDATA = io_input_PWDATA;
  assign io_outputs_8_PADDR = io_input_PADDR;
  assign io_outputs_8_PENABLE = io_input_PENABLE;
  assign io_outputs_8_PSEL[0] = io_input_PSEL[8];
  assign io_outputs_8_PWRITE = io_input_PWRITE;
  assign io_outputs_8_PWDATA = io_input_PWDATA;
  assign _zz_1_ = io_input_PSEL[3];
  assign _zz_2_ = io_input_PSEL[5];
  assign _zz_3_ = io_input_PSEL[6];
  assign _zz_4_ = io_input_PSEL[7];
  assign _zz_5_ = io_input_PSEL[8];
  assign _zz_6_ = (((io_input_PSEL[1] || _zz_1_) || _zz_2_) || _zz_4_);
  assign _zz_7_ = (((io_input_PSEL[2] || _zz_1_) || _zz_3_) || _zz_4_);
  assign _zz_8_ = (((io_input_PSEL[4] || _zz_2_) || _zz_3_) || _zz_4_);
  assign io_input_PREADY = _zz_9_;
  assign io_input_PRDATA = _zz_10_;
  assign io_input_PSLVERROR = _zz_11_;
  always @ (posedge io_clock) begin
    selIndex <= {_zz_5_,{_zz_8_,{_zz_7_,_zz_6_}}};
  end


endmodule

module Hydrogen (
  input               io_clock,
  input               io_reset,
  output              io_sysReset_out,
  input               io_jtag_tms,
  input               io_jtag_tdi,
  output              io_jtag_tdo,
  input               io_jtag_tck,
  input      [11:0]   io_gpio0_pins_read,
  output     [11:0]   io_gpio0_pins_write,
  output     [11:0]   io_gpio0_pins_writeEnable,
  input      [2:0]    io_gpioStatus_pins_read,
  output     [2:0]    io_gpioStatus_pins_write,
  output     [2:0]    io_gpioStatus_pins_writeEnable,
  output              io_uartStd_txd,
  input               io_uartStd_rxd,
  output              io_uartCom_txd,
  input               io_uartCom_rxd,
  output              io_uartRS232_txd,
  input               io_uartRS232_rxd,
  output     [0:0]    io_spi0_ss,
  output              io_spi0_sclk,
  output              io_spi0_mosi,
  input               io_spi0_miso 
);
  wire                _zz_24_;
  wire                _zz_25_;
  wire       [7:0]    _zz_26_;
  wire                _zz_27_;
  wire                _zz_28_;
  wire                _zz_29_;
  reg                 _zz_30_;
  wire       [31:0]   _zz_31_;
  wire       [3:0]    _zz_32_;
  wire       [2:0]    _zz_33_;
  wire                _zz_34_;
  wire                _zz_35_;
  wire       [2:0]    _zz_36_;
  wire       [3:0]    _zz_37_;
  wire       [2:0]    _zz_38_;
  wire       [3:0]    _zz_39_;
  wire                _zz_40_;
  wire                _zz_41_;
  wire                _zz_42_;
  wire                _zz_43_;
  wire                _zz_44_;
  wire       [16:0]   _zz_45_;
  wire       [2:0]    _zz_46_;
  wire       [1:0]    _zz_47_;
  wire       [16:0]   _zz_48_;
  wire       [1:0]    _zz_49_;
  wire                _zz_50_;
  wire       [19:0]   _zz_51_;
  wire       [1:0]    _zz_52_;
  wire       [11:0]   _zz_53_;
  wire       [15:0]   _zz_54_;
  reg        [3:0]    _zz_55_;
  wire       [11:0]   _zz_56_;
  wire       [11:0]   _zz_57_;
  wire       [11:0]   _zz_58_;
  wire       [11:0]   _zz_59_;
  wire       [11:0]   _zz_60_;
  wire       [11:0]   _zz_61_;
  wire       [11:0]   _zz_62_;
  wire                io_reset_buffercc_io_dataOut;
  wire                system_core_cpu_iBus_cmd_valid;
  wire       [31:0]   system_core_cpu_iBus_cmd_payload_pc;
  wire                system_core_cpu_debug_bus_cmd_ready;
  wire       [31:0]   system_core_cpu_debug_bus_rsp_data;
  wire                system_core_cpu_debug_resetOut;
  wire                system_core_cpu_dBus_cmd_valid;
  wire                system_core_cpu_dBus_cmd_payload_wr;
  wire       [31:0]   system_core_cpu_dBus_cmd_payload_address;
  wire       [31:0]   system_core_cpu_dBus_cmd_payload_data;
  wire       [1:0]    system_core_cpu_dBus_cmd_payload_size;
  wire                streamFork_3__io_input_ready;
  wire                streamFork_3__io_outputs_0_valid;
  wire                streamFork_3__io_outputs_0_payload_wr;
  wire       [31:0]   streamFork_3__io_outputs_0_payload_address;
  wire       [31:0]   streamFork_3__io_outputs_0_payload_data;
  wire       [1:0]    streamFork_3__io_outputs_0_payload_size;
  wire                streamFork_3__io_outputs_1_valid;
  wire                streamFork_3__io_outputs_1_payload_wr;
  wire       [31:0]   streamFork_3__io_outputs_1_payload_address;
  wire       [31:0]   streamFork_3__io_outputs_1_payload_data;
  wire       [1:0]    streamFork_3__io_outputs_1_payload_size;
  wire                jtagBridge_1__io_jtag_tdo;
  wire                jtagBridge_1__io_remote_cmd_valid;
  wire                jtagBridge_1__io_remote_cmd_payload_last;
  wire       [0:0]    jtagBridge_1__io_remote_cmd_payload_fragment;
  wire                jtagBridge_1__io_remote_rsp_ready;
  wire                systemDebugger_1__io_remote_cmd_ready;
  wire                systemDebugger_1__io_remote_rsp_valid;
  wire                systemDebugger_1__io_remote_rsp_payload_error;
  wire       [31:0]   systemDebugger_1__io_remote_rsp_payload_data;
  wire                systemDebugger_1__io_mem_cmd_valid;
  wire       [31:0]   systemDebugger_1__io_mem_cmd_payload_address;
  wire       [31:0]   systemDebugger_1__io_mem_cmd_payload_data;
  wire                systemDebugger_1__io_mem_cmd_payload_wr;
  wire       [1:0]    systemDebugger_1__io_mem_cmd_payload_size;
  wire                system_onChipRam_io_axi_arw_ready;
  wire                system_onChipRam_io_axi_w_ready;
  wire                system_onChipRam_io_axi_b_valid;
  wire       [3:0]    system_onChipRam_io_axi_b_payload_id;
  wire       [1:0]    system_onChipRam_io_axi_b_payload_resp;
  wire                system_onChipRam_io_axi_r_valid;
  wire       [31:0]   system_onChipRam_io_axi_r_payload_data;
  wire       [3:0]    system_onChipRam_io_axi_r_payload_id;
  wire       [1:0]    system_onChipRam_io_axi_r_payload_resp;
  wire                system_onChipRam_io_axi_r_payload_last;
  wire                system_apbBridge_io_axi_arw_ready;
  wire                system_apbBridge_io_axi_w_ready;
  wire                system_apbBridge_io_axi_b_valid;
  wire       [3:0]    system_apbBridge_io_axi_b_payload_id;
  wire       [1:0]    system_apbBridge_io_axi_b_payload_resp;
  wire                system_apbBridge_io_axi_r_valid;
  wire       [31:0]   system_apbBridge_io_axi_r_payload_data;
  wire       [3:0]    system_apbBridge_io_axi_r_payload_id;
  wire       [1:0]    system_apbBridge_io_axi_r_payload_resp;
  wire                system_apbBridge_io_axi_r_payload_last;
  wire       [19:0]   system_apbBridge_io_apb_PADDR;
  wire       [0:0]    system_apbBridge_io_apb_PSEL;
  wire                system_apbBridge_io_apb_PENABLE;
  wire                system_apbBridge_io_apb_PWRITE;
  wire       [31:0]   system_apbBridge_io_apb_PWDATA;
  wire                axi4ReadOnlyDecoder_1__io_input_ar_ready;
  wire                axi4ReadOnlyDecoder_1__io_input_r_valid;
  wire       [31:0]   axi4ReadOnlyDecoder_1__io_input_r_payload_data;
  wire       [1:0]    axi4ReadOnlyDecoder_1__io_input_r_payload_resp;
  wire                axi4ReadOnlyDecoder_1__io_input_r_payload_last;
  wire                axi4ReadOnlyDecoder_1__io_outputs_0_ar_valid;
  wire       [31:0]   axi4ReadOnlyDecoder_1__io_outputs_0_ar_payload_addr;
  wire       [3:0]    axi4ReadOnlyDecoder_1__io_outputs_0_ar_payload_cache;
  wire       [2:0]    axi4ReadOnlyDecoder_1__io_outputs_0_ar_payload_prot;
  wire                axi4ReadOnlyDecoder_1__io_outputs_0_r_ready;
  wire                axi4SharedDecoder_1__io_input_arw_ready;
  wire                axi4SharedDecoder_1__io_input_w_ready;
  wire                axi4SharedDecoder_1__io_input_b_valid;
  wire       [1:0]    axi4SharedDecoder_1__io_input_b_payload_resp;
  wire                axi4SharedDecoder_1__io_input_r_valid;
  wire       [31:0]   axi4SharedDecoder_1__io_input_r_payload_data;
  wire       [1:0]    axi4SharedDecoder_1__io_input_r_payload_resp;
  wire                axi4SharedDecoder_1__io_input_r_payload_last;
  wire                axi4SharedDecoder_1__io_sharedOutputs_0_arw_valid;
  wire       [31:0]   axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_addr;
  wire       [2:0]    axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_size;
  wire       [3:0]    axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_cache;
  wire       [2:0]    axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_prot;
  wire                axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_write;
  wire                axi4SharedDecoder_1__io_sharedOutputs_0_w_valid;
  wire       [31:0]   axi4SharedDecoder_1__io_sharedOutputs_0_w_payload_data;
  wire       [3:0]    axi4SharedDecoder_1__io_sharedOutputs_0_w_payload_strb;
  wire                axi4SharedDecoder_1__io_sharedOutputs_0_w_payload_last;
  wire                axi4SharedDecoder_1__io_sharedOutputs_0_b_ready;
  wire                axi4SharedDecoder_1__io_sharedOutputs_0_r_ready;
  wire                axi4SharedDecoder_1__io_sharedOutputs_1_arw_valid;
  wire       [31:0]   axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_addr;
  wire       [2:0]    axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_size;
  wire       [3:0]    axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_cache;
  wire       [2:0]    axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_prot;
  wire                axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_write;
  wire                axi4SharedDecoder_1__io_sharedOutputs_1_w_valid;
  wire       [31:0]   axi4SharedDecoder_1__io_sharedOutputs_1_w_payload_data;
  wire       [3:0]    axi4SharedDecoder_1__io_sharedOutputs_1_w_payload_strb;
  wire                axi4SharedDecoder_1__io_sharedOutputs_1_w_payload_last;
  wire                axi4SharedDecoder_1__io_sharedOutputs_1_b_ready;
  wire                axi4SharedDecoder_1__io_sharedOutputs_1_r_ready;
  wire                system_onChipRam_io_axi_arbiter_io_readInputs_0_ar_ready;
  wire                system_onChipRam_io_axi_arbiter_io_readInputs_0_r_valid;
  wire       [31:0]   system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_data;
  wire       [2:0]    system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_id;
  wire       [1:0]    system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_resp;
  wire                system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_last;
  wire                system_onChipRam_io_axi_arbiter_io_sharedInputs_0_arw_ready;
  wire                system_onChipRam_io_axi_arbiter_io_sharedInputs_0_w_ready;
  wire                system_onChipRam_io_axi_arbiter_io_sharedInputs_0_b_valid;
  wire       [2:0]    system_onChipRam_io_axi_arbiter_io_sharedInputs_0_b_payload_id;
  wire       [1:0]    system_onChipRam_io_axi_arbiter_io_sharedInputs_0_b_payload_resp;
  wire                system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_valid;
  wire       [31:0]   system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_data;
  wire       [2:0]    system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_id;
  wire       [1:0]    system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_resp;
  wire                system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_last;
  wire                system_onChipRam_io_axi_arbiter_io_output_arw_valid;
  wire       [16:0]   system_onChipRam_io_axi_arbiter_io_output_arw_payload_addr;
  wire       [3:0]    system_onChipRam_io_axi_arbiter_io_output_arw_payload_id;
  wire       [7:0]    system_onChipRam_io_axi_arbiter_io_output_arw_payload_len;
  wire       [2:0]    system_onChipRam_io_axi_arbiter_io_output_arw_payload_size;
  wire       [1:0]    system_onChipRam_io_axi_arbiter_io_output_arw_payload_burst;
  wire                system_onChipRam_io_axi_arbiter_io_output_arw_payload_write;
  wire                system_onChipRam_io_axi_arbiter_io_output_w_valid;
  wire       [31:0]   system_onChipRam_io_axi_arbiter_io_output_w_payload_data;
  wire       [3:0]    system_onChipRam_io_axi_arbiter_io_output_w_payload_strb;
  wire                system_onChipRam_io_axi_arbiter_io_output_w_payload_last;
  wire                system_onChipRam_io_axi_arbiter_io_output_b_ready;
  wire                system_onChipRam_io_axi_arbiter_io_output_r_ready;
  wire                system_apbBridge_io_axi_arbiter_io_sharedInputs_0_arw_ready;
  wire                system_apbBridge_io_axi_arbiter_io_sharedInputs_0_w_ready;
  wire                system_apbBridge_io_axi_arbiter_io_sharedInputs_0_b_valid;
  wire       [3:0]    system_apbBridge_io_axi_arbiter_io_sharedInputs_0_b_payload_id;
  wire       [1:0]    system_apbBridge_io_axi_arbiter_io_sharedInputs_0_b_payload_resp;
  wire                system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_valid;
  wire       [31:0]   system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_data;
  wire       [3:0]    system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_id;
  wire       [1:0]    system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_resp;
  wire                system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_last;
  wire                system_apbBridge_io_axi_arbiter_io_output_arw_valid;
  wire       [19:0]   system_apbBridge_io_axi_arbiter_io_output_arw_payload_addr;
  wire       [3:0]    system_apbBridge_io_axi_arbiter_io_output_arw_payload_id;
  wire       [7:0]    system_apbBridge_io_axi_arbiter_io_output_arw_payload_len;
  wire       [2:0]    system_apbBridge_io_axi_arbiter_io_output_arw_payload_size;
  wire       [1:0]    system_apbBridge_io_axi_arbiter_io_output_arw_payload_burst;
  wire                system_apbBridge_io_axi_arbiter_io_output_arw_payload_write;
  wire                system_apbBridge_io_axi_arbiter_io_output_w_valid;
  wire       [31:0]   system_apbBridge_io_axi_arbiter_io_output_w_payload_data;
  wire       [3:0]    system_apbBridge_io_axi_arbiter_io_output_w_payload_strb;
  wire                system_apbBridge_io_axi_arbiter_io_output_w_payload_last;
  wire                system_apbBridge_io_axi_arbiter_io_output_b_ready;
  wire                system_apbBridge_io_axi_arbiter_io_output_r_ready;
  wire                system_mtimerCtrl_io_bus_PREADY;
  wire       [31:0]   system_mtimerCtrl_io_bus_PRDATA;
  wire                system_mtimerCtrl_io_bus_PSLVERROR;
  wire                system_mtimerCtrl_io_interrupt;
  wire                system_plicCtrl_io_bus_PREADY;
  wire       [31:0]   system_plicCtrl_io_bus_PRDATA;
  wire                system_plicCtrl_io_bus_PSLVERROR;
  wire                system_plicCtrl_io_interrupt;
  wire                system_uartStdCtrl_io_bus_PREADY;
  wire       [31:0]   system_uartStdCtrl_io_bus_PRDATA;
  wire                system_uartStdCtrl_io_bus_PSLVERROR;
  wire                system_uartStdCtrl_io_uart_txd;
  wire                system_uartStdCtrl_io_interrupt;
  wire                system_uartComCtrl_io_bus_PREADY;
  wire       [31:0]   system_uartComCtrl_io_bus_PRDATA;
  wire                system_uartComCtrl_io_bus_PSLVERROR;
  wire                system_uartComCtrl_io_uart_txd;
  wire                system_uartComCtrl_io_interrupt;
  wire                system_uartRS232Ctrl_io_bus_PREADY;
  wire       [31:0]   system_uartRS232Ctrl_io_bus_PRDATA;
  wire                system_uartRS232Ctrl_io_bus_PSLVERROR;
  wire                system_uartRS232Ctrl_io_uart_txd;
  wire                system_uartRS232Ctrl_io_interrupt;
  wire                system_gpioStatusCtrl_io_bus_PREADY;
  wire       [31:0]   system_gpioStatusCtrl_io_bus_PRDATA;
  wire                system_gpioStatusCtrl_io_bus_PSLVERROR;
  wire       [2:0]    system_gpioStatusCtrl_io_gpio_pins_write;
  wire       [2:0]    system_gpioStatusCtrl_io_gpio_pins_writeEnable;
  wire       [2:0]    system_gpioStatusCtrl_io_interrupt;
  wire                system_gpio0Ctrl_io_bus_PREADY;
  wire       [31:0]   system_gpio0Ctrl_io_bus_PRDATA;
  wire                system_gpio0Ctrl_io_bus_PSLVERROR;
  wire       [11:0]   system_gpio0Ctrl_io_gpio_pins_write;
  wire       [11:0]   system_gpio0Ctrl_io_gpio_pins_writeEnable;
  wire       [11:0]   system_gpio0Ctrl_io_interrupt;
  wire                system_spiMaster0Ctrl_io_bus_PREADY;
  wire       [31:0]   system_spiMaster0Ctrl_io_bus_PRDATA;
  wire                system_spiMaster0Ctrl_io_bus_PSLVERROR;
  wire       [0:0]    system_spiMaster0Ctrl_io_spi_ss;
  wire                system_spiMaster0Ctrl_io_spi_sclk;
  wire                system_spiMaster0Ctrl_io_spi_mosi;
  wire                system_spiMaster0Ctrl_io_interrupt;
  wire                system_uniqueID0Ctrl_io_bus_PREADY;
  wire       [31:0]   system_uniqueID0Ctrl_io_bus_PRDATA;
  wire                system_uniqueID0Ctrl_io_bus_PSLVERROR;
  wire                io_apb_decoder_io_input_PREADY;
  wire       [31:0]   io_apb_decoder_io_input_PRDATA;
  wire                io_apb_decoder_io_input_PSLVERROR;
  wire       [19:0]   io_apb_decoder_io_output_PADDR;
  wire       [8:0]    io_apb_decoder_io_output_PSEL;
  wire                io_apb_decoder_io_output_PENABLE;
  wire                io_apb_decoder_io_output_PWRITE;
  wire       [31:0]   io_apb_decoder_io_output_PWDATA;
  wire                apb3Router_1__io_input_PREADY;
  wire       [31:0]   apb3Router_1__io_input_PRDATA;
  wire                apb3Router_1__io_input_PSLVERROR;
  wire       [19:0]   apb3Router_1__io_outputs_0_PADDR;
  wire       [0:0]    apb3Router_1__io_outputs_0_PSEL;
  wire                apb3Router_1__io_outputs_0_PENABLE;
  wire                apb3Router_1__io_outputs_0_PWRITE;
  wire       [31:0]   apb3Router_1__io_outputs_0_PWDATA;
  wire       [19:0]   apb3Router_1__io_outputs_1_PADDR;
  wire       [0:0]    apb3Router_1__io_outputs_1_PSEL;
  wire                apb3Router_1__io_outputs_1_PENABLE;
  wire                apb3Router_1__io_outputs_1_PWRITE;
  wire       [31:0]   apb3Router_1__io_outputs_1_PWDATA;
  wire       [19:0]   apb3Router_1__io_outputs_2_PADDR;
  wire       [0:0]    apb3Router_1__io_outputs_2_PSEL;
  wire                apb3Router_1__io_outputs_2_PENABLE;
  wire                apb3Router_1__io_outputs_2_PWRITE;
  wire       [31:0]   apb3Router_1__io_outputs_2_PWDATA;
  wire       [19:0]   apb3Router_1__io_outputs_3_PADDR;
  wire       [0:0]    apb3Router_1__io_outputs_3_PSEL;
  wire                apb3Router_1__io_outputs_3_PENABLE;
  wire                apb3Router_1__io_outputs_3_PWRITE;
  wire       [31:0]   apb3Router_1__io_outputs_3_PWDATA;
  wire       [19:0]   apb3Router_1__io_outputs_4_PADDR;
  wire       [0:0]    apb3Router_1__io_outputs_4_PSEL;
  wire                apb3Router_1__io_outputs_4_PENABLE;
  wire                apb3Router_1__io_outputs_4_PWRITE;
  wire       [31:0]   apb3Router_1__io_outputs_4_PWDATA;
  wire       [19:0]   apb3Router_1__io_outputs_5_PADDR;
  wire       [0:0]    apb3Router_1__io_outputs_5_PSEL;
  wire                apb3Router_1__io_outputs_5_PENABLE;
  wire                apb3Router_1__io_outputs_5_PWRITE;
  wire       [31:0]   apb3Router_1__io_outputs_5_PWDATA;
  wire       [19:0]   apb3Router_1__io_outputs_6_PADDR;
  wire       [0:0]    apb3Router_1__io_outputs_6_PSEL;
  wire                apb3Router_1__io_outputs_6_PENABLE;
  wire                apb3Router_1__io_outputs_6_PWRITE;
  wire       [31:0]   apb3Router_1__io_outputs_6_PWDATA;
  wire       [19:0]   apb3Router_1__io_outputs_7_PADDR;
  wire       [0:0]    apb3Router_1__io_outputs_7_PSEL;
  wire                apb3Router_1__io_outputs_7_PENABLE;
  wire                apb3Router_1__io_outputs_7_PWRITE;
  wire       [31:0]   apb3Router_1__io_outputs_7_PWDATA;
  wire       [19:0]   apb3Router_1__io_outputs_8_PADDR;
  wire       [0:0]    apb3Router_1__io_outputs_8_PSEL;
  wire                apb3Router_1__io_outputs_8_PENABLE;
  wire                apb3Router_1__io_outputs_8_PWRITE;
  wire       [31:0]   apb3Router_1__io_outputs_8_PWDATA;
  wire                _zz_63_;
  wire                _zz_64_;
  wire                _zz_65_;
  wire                _zz_66_;
  wire                _zz_67_;
  wire                _zz_68_;
  wire       [6:0]    _zz_69_;
  reg                 resetCtrl_mainClkResetUnbuffered;
  reg        [5:0]    resetCtrl_systemClkResetCounter = 6'h0;
  wire       [5:0]    _zz_1_;
  reg                 resetCtrl_systemReset;
  reg                 resetCtrl_debugReset;
  wire                system_core_mtimerInterrupt;
  wire                system_core_globalInterrupt;
  reg                 _zz_2_;
  reg                 _zz_3_;
  reg        [2:0]    _zz_4_;
  reg        [2:0]    _zz_5_;
  wire                _zz_6_;
  reg                 streamFork_3__io_outputs_1_thrown_valid;
  wire                streamFork_3__io_outputs_1_thrown_ready;
  wire                streamFork_3__io_outputs_1_thrown_payload_wr;
  wire       [31:0]   streamFork_3__io_outputs_1_thrown_payload_address;
  wire       [31:0]   streamFork_3__io_outputs_1_thrown_payload_data;
  wire       [1:0]    streamFork_3__io_outputs_1_thrown_payload_size;
  reg        [3:0]    _zz_7_;
  reg                 system_core_cpu_debug_resetOut_regNext;
  reg                 _zz_8_;
  wire                _zz_9_;
  wire                _zz_10_;
  reg                 _zz_11_;
  wire                _zz_12_;
  wire                _zz_13_;
  reg                 _zz_14_;
  wire                _zz_15_;
  wire                _zz_16_;
  reg                 _zz_17_;
  wire                axi4SharedDecoder_1__io_input_r_m2sPipe_valid;
  wire                axi4SharedDecoder_1__io_input_r_m2sPipe_ready;
  wire       [31:0]   axi4SharedDecoder_1__io_input_r_m2sPipe_payload_data;
  wire       [1:0]    axi4SharedDecoder_1__io_input_r_m2sPipe_payload_resp;
  wire                axi4SharedDecoder_1__io_input_r_m2sPipe_payload_last;
  reg                 axi4SharedDecoder_1__io_input_r_m2sPipe_rValid;
  reg        [31:0]   axi4SharedDecoder_1__io_input_r_m2sPipe_rData_data;
  reg        [1:0]    axi4SharedDecoder_1__io_input_r_m2sPipe_rData_resp;
  reg                 axi4SharedDecoder_1__io_input_r_m2sPipe_rData_last;
  wire       [2:0]    _zz_18_;
  wire       [7:0]    _zz_19_;
  wire       [2:0]    _zz_20_;
  wire       [7:0]    _zz_21_;
  wire                system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_valid;
  wire                system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_ready;
  wire       [16:0]   system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_addr;
  wire       [3:0]    system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_id;
  wire       [7:0]    system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_len;
  wire       [2:0]    system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_size;
  wire       [1:0]    system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_burst;
  wire                system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_write;
  reg                 system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_valid;
  reg                 system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_ready;
  reg        [16:0]   system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_addr;
  reg        [3:0]    system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_id;
  reg        [7:0]    system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_len;
  reg        [2:0]    system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_size;
  reg        [1:0]    system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_burst;
  reg                 system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_write;
  wire                system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_valid;
  wire                system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_ready;
  wire       [31:0]   system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_payload_data;
  wire       [3:0]    system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_payload_strb;
  wire                system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_payload_last;
  reg                 system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rValid;
  reg        [31:0]   system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rData_data;
  reg        [3:0]    system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rData_strb;
  reg                 system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rData_last;
  wire                system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_valid;
  wire                system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_ready;
  wire       [31:0]   system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_payload_data;
  wire       [3:0]    system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_payload_strb;
  wire                system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_payload_last;
  reg                 system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rValid;
  reg        [31:0]   system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rData_data;
  reg        [3:0]    system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rData_strb;
  reg                 system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rData_last;
  wire       [3:0]    _zz_22_;
  wire       [7:0]    _zz_23_;
  wire                system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_valid;
  wire                system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_ready;
  wire       [19:0]   system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_addr;
  wire       [3:0]    system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_id;
  wire       [7:0]    system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_len;
  wire       [2:0]    system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_size;
  wire       [1:0]    system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_burst;
  wire                system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_write;
  reg                 system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_valid;
  reg                 system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_ready;
  reg        [19:0]   system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_addr;
  reg        [3:0]    system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_id;
  reg        [7:0]    system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_len;
  reg        [2:0]    system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_size;
  reg        [1:0]    system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_burst;
  reg                 system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_write;
  wire                system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_valid;
  wire                system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_ready;
  wire       [31:0]   system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_payload_data;
  wire       [3:0]    system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_payload_strb;
  wire                system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_payload_last;
  reg                 system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_valid;
  reg                 system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_ready;
  reg        [31:0]   system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_payload_data;
  reg        [3:0]    system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_payload_strb;
  reg                 system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_payload_last;

  assign _zz_63_ = (resetCtrl_systemClkResetCounter != _zz_1_);
  assign _zz_64_ = (! streamFork_3__io_outputs_1_payload_wr);
  assign _zz_65_ = (! system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_valid);
  assign _zz_66_ = (_zz_50_ && (! system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_ready));
  assign _zz_67_ = (! system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_valid);
  assign _zz_68_ = (! system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_valid);
  assign _zz_69_ = ({3'd0,_zz_7_} <<< streamFork_3__io_outputs_1_thrown_payload_address[1 : 0]);
  BufferCC_8_ io_reset_buffercc ( 
    .io_dataIn     (io_reset                      ), //i
    .io_dataOut    (io_reset_buffercc_io_dataOut  ), //o
    .io_clock      (io_clock                      )  //i
  );
  VexRiscv system_core_cpu ( 
    .iBus_cmd_valid                   (system_core_cpu_iBus_cmd_valid                              ), //o
    .iBus_cmd_ready                   (axi4ReadOnlyDecoder_1__io_input_ar_ready                    ), //i
    .iBus_cmd_payload_pc              (system_core_cpu_iBus_cmd_payload_pc[31:0]                   ), //o
    .iBus_rsp_valid                   (axi4ReadOnlyDecoder_1__io_input_r_valid                     ), //i
    .iBus_rsp_payload_error           (_zz_24_                                                     ), //i
    .iBus_rsp_payload_inst            (axi4ReadOnlyDecoder_1__io_input_r_payload_data[31:0]        ), //i
    .timerInterrupt                   (system_core_mtimerInterrupt                                 ), //i
    .externalInterrupt                (system_core_globalInterrupt                                 ), //i
    .softwareInterrupt                (_zz_25_                                                     ), //i
    .debug_bus_cmd_valid              (systemDebugger_1__io_mem_cmd_valid                          ), //i
    .debug_bus_cmd_ready              (system_core_cpu_debug_bus_cmd_ready                         ), //o
    .debug_bus_cmd_payload_wr         (systemDebugger_1__io_mem_cmd_payload_wr                     ), //i
    .debug_bus_cmd_payload_address    (_zz_26_[7:0]                                                ), //i
    .debug_bus_cmd_payload_data       (systemDebugger_1__io_mem_cmd_payload_data[31:0]             ), //i
    .debug_bus_rsp_data               (system_core_cpu_debug_bus_rsp_data[31:0]                    ), //o
    .debug_resetOut                   (system_core_cpu_debug_resetOut                              ), //o
    .dBus_cmd_valid                   (system_core_cpu_dBus_cmd_valid                              ), //o
    .dBus_cmd_ready                   (_zz_27_                                                     ), //i
    .dBus_cmd_payload_wr              (system_core_cpu_dBus_cmd_payload_wr                         ), //o
    .dBus_cmd_payload_address         (system_core_cpu_dBus_cmd_payload_address[31:0]              ), //o
    .dBus_cmd_payload_data            (system_core_cpu_dBus_cmd_payload_data[31:0]                 ), //o
    .dBus_cmd_payload_size            (system_core_cpu_dBus_cmd_payload_size[1:0]                  ), //o
    .dBus_rsp_ready                   (axi4SharedDecoder_1__io_input_r_m2sPipe_valid               ), //i
    .dBus_rsp_error                   (_zz_28_                                                     ), //i
    .dBus_rsp_data                    (axi4SharedDecoder_1__io_input_r_m2sPipe_payload_data[31:0]  ), //i
    .io_clock                         (io_clock                                                    ), //i
    .resetCtrl_systemReset            (resetCtrl_systemReset                                       ), //i
    .resetCtrl_debugReset             (resetCtrl_debugReset                                        )  //i
  );
  StreamFork_2_ streamFork_3_ ( 
    .io_input_valid                  (_zz_29_                                           ), //i
    .io_input_ready                  (streamFork_3__io_input_ready                      ), //o
    .io_input_payload_wr             (system_core_cpu_dBus_cmd_payload_wr               ), //i
    .io_input_payload_address        (system_core_cpu_dBus_cmd_payload_address[31:0]    ), //i
    .io_input_payload_data           (system_core_cpu_dBus_cmd_payload_data[31:0]       ), //i
    .io_input_payload_size           (system_core_cpu_dBus_cmd_payload_size[1:0]        ), //i
    .io_outputs_0_valid              (streamFork_3__io_outputs_0_valid                  ), //o
    .io_outputs_0_ready              (axi4SharedDecoder_1__io_input_arw_ready           ), //i
    .io_outputs_0_payload_wr         (streamFork_3__io_outputs_0_payload_wr             ), //o
    .io_outputs_0_payload_address    (streamFork_3__io_outputs_0_payload_address[31:0]  ), //o
    .io_outputs_0_payload_data       (streamFork_3__io_outputs_0_payload_data[31:0]     ), //o
    .io_outputs_0_payload_size       (streamFork_3__io_outputs_0_payload_size[1:0]      ), //o
    .io_outputs_1_valid              (streamFork_3__io_outputs_1_valid                  ), //o
    .io_outputs_1_ready              (_zz_30_                                           ), //i
    .io_outputs_1_payload_wr         (streamFork_3__io_outputs_1_payload_wr             ), //o
    .io_outputs_1_payload_address    (streamFork_3__io_outputs_1_payload_address[31:0]  ), //o
    .io_outputs_1_payload_data       (streamFork_3__io_outputs_1_payload_data[31:0]     ), //o
    .io_outputs_1_payload_size       (streamFork_3__io_outputs_1_payload_size[1:0]      ), //o
    .io_clock                        (io_clock                                          ), //i
    .resetCtrl_systemReset           (resetCtrl_systemReset                             )  //i
  );
  JtagBridge jtagBridge_1_ ( 
    .io_jtag_tms                       (io_jtag_tms                                         ), //i
    .io_jtag_tdi                       (io_jtag_tdi                                         ), //i
    .io_jtag_tdo                       (jtagBridge_1__io_jtag_tdo                           ), //o
    .io_jtag_tck                       (io_jtag_tck                                         ), //i
    .io_remote_cmd_valid               (jtagBridge_1__io_remote_cmd_valid                   ), //o
    .io_remote_cmd_ready               (systemDebugger_1__io_remote_cmd_ready               ), //i
    .io_remote_cmd_payload_last        (jtagBridge_1__io_remote_cmd_payload_last            ), //o
    .io_remote_cmd_payload_fragment    (jtagBridge_1__io_remote_cmd_payload_fragment        ), //o
    .io_remote_rsp_valid               (systemDebugger_1__io_remote_rsp_valid               ), //i
    .io_remote_rsp_ready               (jtagBridge_1__io_remote_rsp_ready                   ), //o
    .io_remote_rsp_payload_error       (systemDebugger_1__io_remote_rsp_payload_error       ), //i
    .io_remote_rsp_payload_data        (systemDebugger_1__io_remote_rsp_payload_data[31:0]  ), //i
    .io_clock                          (io_clock                                            ), //i
    .resetCtrl_debugReset              (resetCtrl_debugReset                                )  //i
  );
  SystemDebugger systemDebugger_1_ ( 
    .io_remote_cmd_valid               (jtagBridge_1__io_remote_cmd_valid                   ), //i
    .io_remote_cmd_ready               (systemDebugger_1__io_remote_cmd_ready               ), //o
    .io_remote_cmd_payload_last        (jtagBridge_1__io_remote_cmd_payload_last            ), //i
    .io_remote_cmd_payload_fragment    (jtagBridge_1__io_remote_cmd_payload_fragment        ), //i
    .io_remote_rsp_valid               (systemDebugger_1__io_remote_rsp_valid               ), //o
    .io_remote_rsp_ready               (jtagBridge_1__io_remote_rsp_ready                   ), //i
    .io_remote_rsp_payload_error       (systemDebugger_1__io_remote_rsp_payload_error       ), //o
    .io_remote_rsp_payload_data        (systemDebugger_1__io_remote_rsp_payload_data[31:0]  ), //o
    .io_mem_cmd_valid                  (systemDebugger_1__io_mem_cmd_valid                  ), //o
    .io_mem_cmd_ready                  (system_core_cpu_debug_bus_cmd_ready                 ), //i
    .io_mem_cmd_payload_address        (systemDebugger_1__io_mem_cmd_payload_address[31:0]  ), //o
    .io_mem_cmd_payload_data           (systemDebugger_1__io_mem_cmd_payload_data[31:0]     ), //o
    .io_mem_cmd_payload_wr             (systemDebugger_1__io_mem_cmd_payload_wr             ), //o
    .io_mem_cmd_payload_size           (systemDebugger_1__io_mem_cmd_payload_size[1:0]      ), //o
    .io_mem_rsp_valid                  (_zz_8_                                              ), //i
    .io_mem_rsp_payload                (system_core_cpu_debug_bus_rsp_data[31:0]            ), //i
    .io_clock                          (io_clock                                            ), //i
    .resetCtrl_debugReset              (resetCtrl_debugReset                                )  //i
  );
  Axi4SharedOnChipRam system_onChipRam ( 
    .io_axi_arw_valid            (system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_valid                    ), //i
    .io_axi_arw_ready            (system_onChipRam_io_axi_arw_ready                                               ), //o
    .io_axi_arw_payload_addr     (system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_addr[16:0]       ), //i
    .io_axi_arw_payload_id       (system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_id[3:0]          ), //i
    .io_axi_arw_payload_len      (system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_len[7:0]         ), //i
    .io_axi_arw_payload_size     (system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_size[2:0]        ), //i
    .io_axi_arw_payload_burst    (system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_burst[1:0]       ), //i
    .io_axi_arw_payload_write    (system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_write            ), //i
    .io_axi_w_valid              (system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_valid               ), //i
    .io_axi_w_ready              (system_onChipRam_io_axi_w_ready                                                 ), //o
    .io_axi_w_payload_data       (system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_payload_data[31:0]  ), //i
    .io_axi_w_payload_strb       (system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_payload_strb[3:0]   ), //i
    .io_axi_w_payload_last       (system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_payload_last        ), //i
    .io_axi_b_valid              (system_onChipRam_io_axi_b_valid                                                 ), //o
    .io_axi_b_ready              (system_onChipRam_io_axi_arbiter_io_output_b_ready                               ), //i
    .io_axi_b_payload_id         (system_onChipRam_io_axi_b_payload_id[3:0]                                       ), //o
    .io_axi_b_payload_resp       (system_onChipRam_io_axi_b_payload_resp[1:0]                                     ), //o
    .io_axi_r_valid              (system_onChipRam_io_axi_r_valid                                                 ), //o
    .io_axi_r_ready              (system_onChipRam_io_axi_arbiter_io_output_r_ready                               ), //i
    .io_axi_r_payload_data       (system_onChipRam_io_axi_r_payload_data[31:0]                                    ), //o
    .io_axi_r_payload_id         (system_onChipRam_io_axi_r_payload_id[3:0]                                       ), //o
    .io_axi_r_payload_resp       (system_onChipRam_io_axi_r_payload_resp[1:0]                                     ), //o
    .io_axi_r_payload_last       (system_onChipRam_io_axi_r_payload_last                                          ), //o
    .io_clock                    (io_clock                                                                        ), //i
    .resetCtrl_systemReset       (resetCtrl_systemReset                                                           )  //i
  );
  Axi4SharedToApb3Bridge system_apbBridge ( 
    .io_axi_arw_valid            (system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_valid               ), //i
    .io_axi_arw_ready            (system_apbBridge_io_axi_arw_ready                                          ), //o
    .io_axi_arw_payload_addr     (system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_addr[19:0]  ), //i
    .io_axi_arw_payload_id       (system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_id[3:0]     ), //i
    .io_axi_arw_payload_len      (system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_len[7:0]    ), //i
    .io_axi_arw_payload_size     (system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_size[2:0]   ), //i
    .io_axi_arw_payload_burst    (system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_burst[1:0]  ), //i
    .io_axi_arw_payload_write    (system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_write       ), //i
    .io_axi_w_valid              (system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_valid                 ), //i
    .io_axi_w_ready              (system_apbBridge_io_axi_w_ready                                            ), //o
    .io_axi_w_payload_data       (system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_payload_data[31:0]    ), //i
    .io_axi_w_payload_strb       (system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_payload_strb[3:0]     ), //i
    .io_axi_w_payload_last       (system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_payload_last          ), //i
    .io_axi_b_valid              (system_apbBridge_io_axi_b_valid                                            ), //o
    .io_axi_b_ready              (system_apbBridge_io_axi_arbiter_io_output_b_ready                          ), //i
    .io_axi_b_payload_id         (system_apbBridge_io_axi_b_payload_id[3:0]                                  ), //o
    .io_axi_b_payload_resp       (system_apbBridge_io_axi_b_payload_resp[1:0]                                ), //o
    .io_axi_r_valid              (system_apbBridge_io_axi_r_valid                                            ), //o
    .io_axi_r_ready              (system_apbBridge_io_axi_arbiter_io_output_r_ready                          ), //i
    .io_axi_r_payload_data       (system_apbBridge_io_axi_r_payload_data[31:0]                               ), //o
    .io_axi_r_payload_id         (system_apbBridge_io_axi_r_payload_id[3:0]                                  ), //o
    .io_axi_r_payload_resp       (system_apbBridge_io_axi_r_payload_resp[1:0]                                ), //o
    .io_axi_r_payload_last       (system_apbBridge_io_axi_r_payload_last                                     ), //o
    .io_apb_PADDR                (system_apbBridge_io_apb_PADDR[19:0]                                        ), //o
    .io_apb_PSEL                 (system_apbBridge_io_apb_PSEL                                               ), //o
    .io_apb_PENABLE              (system_apbBridge_io_apb_PENABLE                                            ), //o
    .io_apb_PREADY               (io_apb_decoder_io_input_PREADY                                             ), //i
    .io_apb_PWRITE               (system_apbBridge_io_apb_PWRITE                                             ), //o
    .io_apb_PWDATA               (system_apbBridge_io_apb_PWDATA[31:0]                                       ), //o
    .io_apb_PRDATA               (io_apb_decoder_io_input_PRDATA[31:0]                                       ), //i
    .io_apb_PSLVERROR            (io_apb_decoder_io_input_PSLVERROR                                          ), //i
    .io_clock                    (io_clock                                                                   ), //i
    .resetCtrl_systemReset       (resetCtrl_systemReset                                                      )  //i
  );
  Axi4ReadOnlyDecoder axi4ReadOnlyDecoder_1_ ( 
    .io_input_ar_valid                (system_core_cpu_iBus_cmd_valid                                        ), //i
    .io_input_ar_ready                (axi4ReadOnlyDecoder_1__io_input_ar_ready                              ), //o
    .io_input_ar_payload_addr         (_zz_31_[31:0]                                                         ), //i
    .io_input_ar_payload_cache        (_zz_32_[3:0]                                                          ), //i
    .io_input_ar_payload_prot         (_zz_33_[2:0]                                                          ), //i
    .io_input_r_valid                 (axi4ReadOnlyDecoder_1__io_input_r_valid                               ), //o
    .io_input_r_ready                 (_zz_34_                                                               ), //i
    .io_input_r_payload_data          (axi4ReadOnlyDecoder_1__io_input_r_payload_data[31:0]                  ), //o
    .io_input_r_payload_resp          (axi4ReadOnlyDecoder_1__io_input_r_payload_resp[1:0]                   ), //o
    .io_input_r_payload_last          (axi4ReadOnlyDecoder_1__io_input_r_payload_last                        ), //o
    .io_outputs_0_ar_valid            (axi4ReadOnlyDecoder_1__io_outputs_0_ar_valid                          ), //o
    .io_outputs_0_ar_ready            (_zz_35_                                                               ), //i
    .io_outputs_0_ar_payload_addr     (axi4ReadOnlyDecoder_1__io_outputs_0_ar_payload_addr[31:0]             ), //o
    .io_outputs_0_ar_payload_cache    (axi4ReadOnlyDecoder_1__io_outputs_0_ar_payload_cache[3:0]             ), //o
    .io_outputs_0_ar_payload_prot     (axi4ReadOnlyDecoder_1__io_outputs_0_ar_payload_prot[2:0]              ), //o
    .io_outputs_0_r_valid             (system_onChipRam_io_axi_arbiter_io_readInputs_0_r_valid               ), //i
    .io_outputs_0_r_ready             (axi4ReadOnlyDecoder_1__io_outputs_0_r_ready                           ), //o
    .io_outputs_0_r_payload_data      (system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_data[31:0]  ), //i
    .io_outputs_0_r_payload_resp      (system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_resp[1:0]   ), //i
    .io_outputs_0_r_payload_last      (system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_last        ), //i
    .io_clock                         (io_clock                                                              ), //i
    .resetCtrl_systemReset            (resetCtrl_systemReset                                                 )  //i
  );
  Axi4SharedDecoder axi4SharedDecoder_1_ ( 
    .io_input_arw_valid                      (streamFork_3__io_outputs_0_valid                                        ), //i
    .io_input_arw_ready                      (axi4SharedDecoder_1__io_input_arw_ready                                 ), //o
    .io_input_arw_payload_addr               (streamFork_3__io_outputs_0_payload_address[31:0]                        ), //i
    .io_input_arw_payload_size               (_zz_36_[2:0]                                                            ), //i
    .io_input_arw_payload_cache              (_zz_37_[3:0]                                                            ), //i
    .io_input_arw_payload_prot               (_zz_38_[2:0]                                                            ), //i
    .io_input_arw_payload_write              (streamFork_3__io_outputs_0_payload_wr                                   ), //i
    .io_input_w_valid                        (streamFork_3__io_outputs_1_thrown_valid                                 ), //i
    .io_input_w_ready                        (axi4SharedDecoder_1__io_input_w_ready                                   ), //o
    .io_input_w_payload_data                 (streamFork_3__io_outputs_1_thrown_payload_data[31:0]                    ), //i
    .io_input_w_payload_strb                 (_zz_39_[3:0]                                                            ), //i
    .io_input_w_payload_last                 (_zz_40_                                                                 ), //i
    .io_input_b_valid                        (axi4SharedDecoder_1__io_input_b_valid                                   ), //o
    .io_input_b_ready                        (_zz_41_                                                                 ), //i
    .io_input_b_payload_resp                 (axi4SharedDecoder_1__io_input_b_payload_resp[1:0]                       ), //o
    .io_input_r_valid                        (axi4SharedDecoder_1__io_input_r_valid                                   ), //o
    .io_input_r_ready                        (_zz_42_                                                                 ), //i
    .io_input_r_payload_data                 (axi4SharedDecoder_1__io_input_r_payload_data[31:0]                      ), //o
    .io_input_r_payload_resp                 (axi4SharedDecoder_1__io_input_r_payload_resp[1:0]                       ), //o
    .io_input_r_payload_last                 (axi4SharedDecoder_1__io_input_r_payload_last                            ), //o
    .io_sharedOutputs_0_arw_valid            (axi4SharedDecoder_1__io_sharedOutputs_0_arw_valid                       ), //o
    .io_sharedOutputs_0_arw_ready            (_zz_43_                                                                 ), //i
    .io_sharedOutputs_0_arw_payload_addr     (axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_addr[31:0]          ), //o
    .io_sharedOutputs_0_arw_payload_size     (axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_size[2:0]           ), //o
    .io_sharedOutputs_0_arw_payload_cache    (axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_cache[3:0]          ), //o
    .io_sharedOutputs_0_arw_payload_prot     (axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_prot[2:0]           ), //o
    .io_sharedOutputs_0_arw_payload_write    (axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_write               ), //o
    .io_sharedOutputs_0_w_valid              (axi4SharedDecoder_1__io_sharedOutputs_0_w_valid                         ), //o
    .io_sharedOutputs_0_w_ready              (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_w_ready               ), //i
    .io_sharedOutputs_0_w_payload_data       (axi4SharedDecoder_1__io_sharedOutputs_0_w_payload_data[31:0]            ), //o
    .io_sharedOutputs_0_w_payload_strb       (axi4SharedDecoder_1__io_sharedOutputs_0_w_payload_strb[3:0]             ), //o
    .io_sharedOutputs_0_w_payload_last       (axi4SharedDecoder_1__io_sharedOutputs_0_w_payload_last                  ), //o
    .io_sharedOutputs_0_b_valid              (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_b_valid               ), //i
    .io_sharedOutputs_0_b_ready              (axi4SharedDecoder_1__io_sharedOutputs_0_b_ready                         ), //o
    .io_sharedOutputs_0_b_payload_resp       (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_b_payload_resp[1:0]   ), //i
    .io_sharedOutputs_0_r_valid              (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_valid               ), //i
    .io_sharedOutputs_0_r_ready              (axi4SharedDecoder_1__io_sharedOutputs_0_r_ready                         ), //o
    .io_sharedOutputs_0_r_payload_data       (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_data[31:0]  ), //i
    .io_sharedOutputs_0_r_payload_resp       (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_resp[1:0]   ), //i
    .io_sharedOutputs_0_r_payload_last       (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_last        ), //i
    .io_sharedOutputs_1_arw_valid            (axi4SharedDecoder_1__io_sharedOutputs_1_arw_valid                       ), //o
    .io_sharedOutputs_1_arw_ready            (_zz_44_                                                                 ), //i
    .io_sharedOutputs_1_arw_payload_addr     (axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_addr[31:0]          ), //o
    .io_sharedOutputs_1_arw_payload_size     (axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_size[2:0]           ), //o
    .io_sharedOutputs_1_arw_payload_cache    (axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_cache[3:0]          ), //o
    .io_sharedOutputs_1_arw_payload_prot     (axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_prot[2:0]           ), //o
    .io_sharedOutputs_1_arw_payload_write    (axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_write               ), //o
    .io_sharedOutputs_1_w_valid              (axi4SharedDecoder_1__io_sharedOutputs_1_w_valid                         ), //o
    .io_sharedOutputs_1_w_ready              (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_w_ready               ), //i
    .io_sharedOutputs_1_w_payload_data       (axi4SharedDecoder_1__io_sharedOutputs_1_w_payload_data[31:0]            ), //o
    .io_sharedOutputs_1_w_payload_strb       (axi4SharedDecoder_1__io_sharedOutputs_1_w_payload_strb[3:0]             ), //o
    .io_sharedOutputs_1_w_payload_last       (axi4SharedDecoder_1__io_sharedOutputs_1_w_payload_last                  ), //o
    .io_sharedOutputs_1_b_valid              (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_b_valid               ), //i
    .io_sharedOutputs_1_b_ready              (axi4SharedDecoder_1__io_sharedOutputs_1_b_ready                         ), //o
    .io_sharedOutputs_1_b_payload_resp       (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_b_payload_resp[1:0]   ), //i
    .io_sharedOutputs_1_r_valid              (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_valid               ), //i
    .io_sharedOutputs_1_r_ready              (axi4SharedDecoder_1__io_sharedOutputs_1_r_ready                         ), //o
    .io_sharedOutputs_1_r_payload_data       (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_data[31:0]  ), //i
    .io_sharedOutputs_1_r_payload_resp       (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_resp[1:0]   ), //i
    .io_sharedOutputs_1_r_payload_last       (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_last        ), //i
    .io_clock                                (io_clock                                                                ), //i
    .resetCtrl_systemReset                   (resetCtrl_systemReset                                                   )  //i
  );
  Axi4SharedArbiter system_onChipRam_io_axi_arbiter ( 
    .io_readInputs_0_ar_valid               (_zz_9_                                                                  ), //i
    .io_readInputs_0_ar_ready               (system_onChipRam_io_axi_arbiter_io_readInputs_0_ar_ready                ), //o
    .io_readInputs_0_ar_payload_addr        (_zz_45_[16:0]                                                           ), //i
    .io_readInputs_0_ar_payload_id          (_zz_18_[2:0]                                                            ), //i
    .io_readInputs_0_ar_payload_len         (_zz_19_[7:0]                                                            ), //i
    .io_readInputs_0_ar_payload_size        (_zz_46_[2:0]                                                            ), //i
    .io_readInputs_0_ar_payload_burst       (_zz_47_[1:0]                                                            ), //i
    .io_readInputs_0_r_valid                (system_onChipRam_io_axi_arbiter_io_readInputs_0_r_valid                 ), //o
    .io_readInputs_0_r_ready                (axi4ReadOnlyDecoder_1__io_outputs_0_r_ready                             ), //i
    .io_readInputs_0_r_payload_data         (system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_data[31:0]    ), //o
    .io_readInputs_0_r_payload_id           (system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_id[2:0]       ), //o
    .io_readInputs_0_r_payload_resp         (system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_resp[1:0]     ), //o
    .io_readInputs_0_r_payload_last         (system_onChipRam_io_axi_arbiter_io_readInputs_0_r_payload_last          ), //o
    .io_sharedInputs_0_arw_valid            (_zz_12_                                                                 ), //i
    .io_sharedInputs_0_arw_ready            (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_arw_ready             ), //o
    .io_sharedInputs_0_arw_payload_addr     (_zz_48_[16:0]                                                           ), //i
    .io_sharedInputs_0_arw_payload_id       (_zz_20_[2:0]                                                            ), //i
    .io_sharedInputs_0_arw_payload_len      (_zz_21_[7:0]                                                            ), //i
    .io_sharedInputs_0_arw_payload_size     (axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_size[2:0]           ), //i
    .io_sharedInputs_0_arw_payload_burst    (_zz_49_[1:0]                                                            ), //i
    .io_sharedInputs_0_arw_payload_write    (axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_write               ), //i
    .io_sharedInputs_0_w_valid              (axi4SharedDecoder_1__io_sharedOutputs_0_w_valid                         ), //i
    .io_sharedInputs_0_w_ready              (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_w_ready               ), //o
    .io_sharedInputs_0_w_payload_data       (axi4SharedDecoder_1__io_sharedOutputs_0_w_payload_data[31:0]            ), //i
    .io_sharedInputs_0_w_payload_strb       (axi4SharedDecoder_1__io_sharedOutputs_0_w_payload_strb[3:0]             ), //i
    .io_sharedInputs_0_w_payload_last       (axi4SharedDecoder_1__io_sharedOutputs_0_w_payload_last                  ), //i
    .io_sharedInputs_0_b_valid              (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_b_valid               ), //o
    .io_sharedInputs_0_b_ready              (axi4SharedDecoder_1__io_sharedOutputs_0_b_ready                         ), //i
    .io_sharedInputs_0_b_payload_id         (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_b_payload_id[2:0]     ), //o
    .io_sharedInputs_0_b_payload_resp       (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_b_payload_resp[1:0]   ), //o
    .io_sharedInputs_0_r_valid              (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_valid               ), //o
    .io_sharedInputs_0_r_ready              (axi4SharedDecoder_1__io_sharedOutputs_0_r_ready                         ), //i
    .io_sharedInputs_0_r_payload_data       (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_data[31:0]  ), //o
    .io_sharedInputs_0_r_payload_id         (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_id[2:0]     ), //o
    .io_sharedInputs_0_r_payload_resp       (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_resp[1:0]   ), //o
    .io_sharedInputs_0_r_payload_last       (system_onChipRam_io_axi_arbiter_io_sharedInputs_0_r_payload_last        ), //o
    .io_output_arw_valid                    (system_onChipRam_io_axi_arbiter_io_output_arw_valid                     ), //o
    .io_output_arw_ready                    (system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_ready       ), //i
    .io_output_arw_payload_addr             (system_onChipRam_io_axi_arbiter_io_output_arw_payload_addr[16:0]        ), //o
    .io_output_arw_payload_id               (system_onChipRam_io_axi_arbiter_io_output_arw_payload_id[3:0]           ), //o
    .io_output_arw_payload_len              (system_onChipRam_io_axi_arbiter_io_output_arw_payload_len[7:0]          ), //o
    .io_output_arw_payload_size             (system_onChipRam_io_axi_arbiter_io_output_arw_payload_size[2:0]         ), //o
    .io_output_arw_payload_burst            (system_onChipRam_io_axi_arbiter_io_output_arw_payload_burst[1:0]        ), //o
    .io_output_arw_payload_write            (system_onChipRam_io_axi_arbiter_io_output_arw_payload_write             ), //o
    .io_output_w_valid                      (system_onChipRam_io_axi_arbiter_io_output_w_valid                       ), //o
    .io_output_w_ready                      (_zz_50_                                                                 ), //i
    .io_output_w_payload_data               (system_onChipRam_io_axi_arbiter_io_output_w_payload_data[31:0]          ), //o
    .io_output_w_payload_strb               (system_onChipRam_io_axi_arbiter_io_output_w_payload_strb[3:0]           ), //o
    .io_output_w_payload_last               (system_onChipRam_io_axi_arbiter_io_output_w_payload_last                ), //o
    .io_output_b_valid                      (system_onChipRam_io_axi_b_valid                                         ), //i
    .io_output_b_ready                      (system_onChipRam_io_axi_arbiter_io_output_b_ready                       ), //o
    .io_output_b_payload_id                 (system_onChipRam_io_axi_b_payload_id[3:0]                               ), //i
    .io_output_b_payload_resp               (system_onChipRam_io_axi_b_payload_resp[1:0]                             ), //i
    .io_output_r_valid                      (system_onChipRam_io_axi_r_valid                                         ), //i
    .io_output_r_ready                      (system_onChipRam_io_axi_arbiter_io_output_r_ready                       ), //o
    .io_output_r_payload_data               (system_onChipRam_io_axi_r_payload_data[31:0]                            ), //i
    .io_output_r_payload_id                 (system_onChipRam_io_axi_r_payload_id[3:0]                               ), //i
    .io_output_r_payload_resp               (system_onChipRam_io_axi_r_payload_resp[1:0]                             ), //i
    .io_output_r_payload_last               (system_onChipRam_io_axi_r_payload_last                                  ), //i
    .io_clock                               (io_clock                                                                ), //i
    .resetCtrl_systemReset                  (resetCtrl_systemReset                                                   )  //i
  );
  Axi4SharedArbiter_1_ system_apbBridge_io_axi_arbiter ( 
    .io_sharedInputs_0_arw_valid            (_zz_15_                                                                 ), //i
    .io_sharedInputs_0_arw_ready            (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_arw_ready             ), //o
    .io_sharedInputs_0_arw_payload_addr     (_zz_51_[19:0]                                                           ), //i
    .io_sharedInputs_0_arw_payload_id       (_zz_22_[3:0]                                                            ), //i
    .io_sharedInputs_0_arw_payload_len      (_zz_23_[7:0]                                                            ), //i
    .io_sharedInputs_0_arw_payload_size     (axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_size[2:0]           ), //i
    .io_sharedInputs_0_arw_payload_burst    (_zz_52_[1:0]                                                            ), //i
    .io_sharedInputs_0_arw_payload_write    (axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_write               ), //i
    .io_sharedInputs_0_w_valid              (axi4SharedDecoder_1__io_sharedOutputs_1_w_valid                         ), //i
    .io_sharedInputs_0_w_ready              (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_w_ready               ), //o
    .io_sharedInputs_0_w_payload_data       (axi4SharedDecoder_1__io_sharedOutputs_1_w_payload_data[31:0]            ), //i
    .io_sharedInputs_0_w_payload_strb       (axi4SharedDecoder_1__io_sharedOutputs_1_w_payload_strb[3:0]             ), //i
    .io_sharedInputs_0_w_payload_last       (axi4SharedDecoder_1__io_sharedOutputs_1_w_payload_last                  ), //i
    .io_sharedInputs_0_b_valid              (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_b_valid               ), //o
    .io_sharedInputs_0_b_ready              (axi4SharedDecoder_1__io_sharedOutputs_1_b_ready                         ), //i
    .io_sharedInputs_0_b_payload_id         (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_b_payload_id[3:0]     ), //o
    .io_sharedInputs_0_b_payload_resp       (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_b_payload_resp[1:0]   ), //o
    .io_sharedInputs_0_r_valid              (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_valid               ), //o
    .io_sharedInputs_0_r_ready              (axi4SharedDecoder_1__io_sharedOutputs_1_r_ready                         ), //i
    .io_sharedInputs_0_r_payload_data       (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_data[31:0]  ), //o
    .io_sharedInputs_0_r_payload_id         (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_id[3:0]     ), //o
    .io_sharedInputs_0_r_payload_resp       (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_resp[1:0]   ), //o
    .io_sharedInputs_0_r_payload_last       (system_apbBridge_io_axi_arbiter_io_sharedInputs_0_r_payload_last        ), //o
    .io_output_arw_valid                    (system_apbBridge_io_axi_arbiter_io_output_arw_valid                     ), //o
    .io_output_arw_ready                    (system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_ready       ), //i
    .io_output_arw_payload_addr             (system_apbBridge_io_axi_arbiter_io_output_arw_payload_addr[19:0]        ), //o
    .io_output_arw_payload_id               (system_apbBridge_io_axi_arbiter_io_output_arw_payload_id[3:0]           ), //o
    .io_output_arw_payload_len              (system_apbBridge_io_axi_arbiter_io_output_arw_payload_len[7:0]          ), //o
    .io_output_arw_payload_size             (system_apbBridge_io_axi_arbiter_io_output_arw_payload_size[2:0]         ), //o
    .io_output_arw_payload_burst            (system_apbBridge_io_axi_arbiter_io_output_arw_payload_burst[1:0]        ), //o
    .io_output_arw_payload_write            (system_apbBridge_io_axi_arbiter_io_output_arw_payload_write             ), //o
    .io_output_w_valid                      (system_apbBridge_io_axi_arbiter_io_output_w_valid                       ), //o
    .io_output_w_ready                      (system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_ready         ), //i
    .io_output_w_payload_data               (system_apbBridge_io_axi_arbiter_io_output_w_payload_data[31:0]          ), //o
    .io_output_w_payload_strb               (system_apbBridge_io_axi_arbiter_io_output_w_payload_strb[3:0]           ), //o
    .io_output_w_payload_last               (system_apbBridge_io_axi_arbiter_io_output_w_payload_last                ), //o
    .io_output_b_valid                      (system_apbBridge_io_axi_b_valid                                         ), //i
    .io_output_b_ready                      (system_apbBridge_io_axi_arbiter_io_output_b_ready                       ), //o
    .io_output_b_payload_id                 (system_apbBridge_io_axi_b_payload_id[3:0]                               ), //i
    .io_output_b_payload_resp               (system_apbBridge_io_axi_b_payload_resp[1:0]                             ), //i
    .io_output_r_valid                      (system_apbBridge_io_axi_r_valid                                         ), //i
    .io_output_r_ready                      (system_apbBridge_io_axi_arbiter_io_output_r_ready                       ), //o
    .io_output_r_payload_data               (system_apbBridge_io_axi_r_payload_data[31:0]                            ), //i
    .io_output_r_payload_id                 (system_apbBridge_io_axi_r_payload_id[3:0]                               ), //i
    .io_output_r_payload_resp               (system_apbBridge_io_axi_r_payload_resp[1:0]                             ), //i
    .io_output_r_payload_last               (system_apbBridge_io_axi_r_payload_last                                  ), //i
    .io_clock                               (io_clock                                                                ), //i
    .resetCtrl_systemReset                  (resetCtrl_systemReset                                                   )  //i
  );
  Apb3MachineTimer system_mtimerCtrl ( 
    .io_bus_PADDR             (_zz_53_[11:0]                            ), //i
    .io_bus_PSEL              (apb3Router_1__io_outputs_0_PSEL          ), //i
    .io_bus_PENABLE           (apb3Router_1__io_outputs_0_PENABLE       ), //i
    .io_bus_PREADY            (system_mtimerCtrl_io_bus_PREADY          ), //o
    .io_bus_PWRITE            (apb3Router_1__io_outputs_0_PWRITE        ), //i
    .io_bus_PWDATA            (apb3Router_1__io_outputs_0_PWDATA[31:0]  ), //i
    .io_bus_PRDATA            (system_mtimerCtrl_io_bus_PRDATA[31:0]    ), //o
    .io_bus_PSLVERROR         (system_mtimerCtrl_io_bus_PSLVERROR       ), //o
    .io_interrupt             (system_mtimerCtrl_io_interrupt           ), //o
    .io_clock                 (io_clock                                 ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                    )  //i
  );
  Apb3Plic system_plicCtrl ( 
    .io_bus_PADDR             (_zz_54_[15:0]                            ), //i
    .io_bus_PSEL              (apb3Router_1__io_outputs_1_PSEL          ), //i
    .io_bus_PENABLE           (apb3Router_1__io_outputs_1_PENABLE       ), //i
    .io_bus_PREADY            (system_plicCtrl_io_bus_PREADY            ), //o
    .io_bus_PWRITE            (apb3Router_1__io_outputs_1_PWRITE        ), //i
    .io_bus_PWDATA            (apb3Router_1__io_outputs_1_PWDATA[31:0]  ), //i
    .io_bus_PRDATA            (system_plicCtrl_io_bus_PRDATA[31:0]      ), //o
    .io_bus_PSLVERROR         (system_plicCtrl_io_bus_PSLVERROR         ), //o
    .io_interrupt             (system_plicCtrl_io_interrupt             ), //o
    .io_sources               (_zz_55_[3:0]                             ), //i
    .io_clock                 (io_clock                                 ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                    )  //i
  );
  Apb3Uart system_uartStdCtrl ( 
    .io_bus_PADDR             (_zz_56_[11:0]                            ), //i
    .io_bus_PSEL              (apb3Router_1__io_outputs_2_PSEL          ), //i
    .io_bus_PENABLE           (apb3Router_1__io_outputs_2_PENABLE       ), //i
    .io_bus_PREADY            (system_uartStdCtrl_io_bus_PREADY         ), //o
    .io_bus_PWRITE            (apb3Router_1__io_outputs_2_PWRITE        ), //i
    .io_bus_PWDATA            (apb3Router_1__io_outputs_2_PWDATA[31:0]  ), //i
    .io_bus_PRDATA            (system_uartStdCtrl_io_bus_PRDATA[31:0]   ), //o
    .io_bus_PSLVERROR         (system_uartStdCtrl_io_bus_PSLVERROR      ), //o
    .io_uart_txd              (system_uartStdCtrl_io_uart_txd           ), //o
    .io_uart_rxd              (io_uartStd_rxd                           ), //i
    .io_interrupt             (system_uartStdCtrl_io_interrupt          ), //o
    .io_clock                 (io_clock                                 ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                    )  //i
  );
  Apb3Uart_1_ system_uartComCtrl ( 
    .io_bus_PADDR             (_zz_57_[11:0]                            ), //i
    .io_bus_PSEL              (apb3Router_1__io_outputs_3_PSEL          ), //i
    .io_bus_PENABLE           (apb3Router_1__io_outputs_3_PENABLE       ), //i
    .io_bus_PREADY            (system_uartComCtrl_io_bus_PREADY         ), //o
    .io_bus_PWRITE            (apb3Router_1__io_outputs_3_PWRITE        ), //i
    .io_bus_PWDATA            (apb3Router_1__io_outputs_3_PWDATA[31:0]  ), //i
    .io_bus_PRDATA            (system_uartComCtrl_io_bus_PRDATA[31:0]   ), //o
    .io_bus_PSLVERROR         (system_uartComCtrl_io_bus_PSLVERROR      ), //o
    .io_uart_txd              (system_uartComCtrl_io_uart_txd           ), //o
    .io_uart_rxd              (io_uartCom_rxd                           ), //i
    .io_interrupt             (system_uartComCtrl_io_interrupt          ), //o
    .io_clock                 (io_clock                                 ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                    )  //i
  );
  Apb3Uart_1_ system_uartRS232Ctrl ( 
    .io_bus_PADDR             (_zz_58_[11:0]                             ), //i
    .io_bus_PSEL              (apb3Router_1__io_outputs_4_PSEL           ), //i
    .io_bus_PENABLE           (apb3Router_1__io_outputs_4_PENABLE        ), //i
    .io_bus_PREADY            (system_uartRS232Ctrl_io_bus_PREADY        ), //o
    .io_bus_PWRITE            (apb3Router_1__io_outputs_4_PWRITE         ), //i
    .io_bus_PWDATA            (apb3Router_1__io_outputs_4_PWDATA[31:0]   ), //i
    .io_bus_PRDATA            (system_uartRS232Ctrl_io_bus_PRDATA[31:0]  ), //o
    .io_bus_PSLVERROR         (system_uartRS232Ctrl_io_bus_PSLVERROR     ), //o
    .io_uart_txd              (system_uartRS232Ctrl_io_uart_txd          ), //o
    .io_uart_rxd              (io_uartRS232_rxd                          ), //i
    .io_interrupt             (system_uartRS232Ctrl_io_interrupt         ), //o
    .io_clock                 (io_clock                                  ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                     )  //i
  );
  Apb3Gpio system_gpioStatusCtrl ( 
    .io_bus_PADDR                (_zz_59_[11:0]                                        ), //i
    .io_bus_PSEL                 (apb3Router_1__io_outputs_5_PSEL                      ), //i
    .io_bus_PENABLE              (apb3Router_1__io_outputs_5_PENABLE                   ), //i
    .io_bus_PREADY               (system_gpioStatusCtrl_io_bus_PREADY                  ), //o
    .io_bus_PWRITE               (apb3Router_1__io_outputs_5_PWRITE                    ), //i
    .io_bus_PWDATA               (apb3Router_1__io_outputs_5_PWDATA[31:0]              ), //i
    .io_bus_PRDATA               (system_gpioStatusCtrl_io_bus_PRDATA[31:0]            ), //o
    .io_bus_PSLVERROR            (system_gpioStatusCtrl_io_bus_PSLVERROR               ), //o
    .io_gpio_pins_read           (io_gpioStatus_pins_read[2:0]                         ), //i
    .io_gpio_pins_write          (system_gpioStatusCtrl_io_gpio_pins_write[2:0]        ), //o
    .io_gpio_pins_writeEnable    (system_gpioStatusCtrl_io_gpio_pins_writeEnable[2:0]  ), //o
    .io_interrupt                (system_gpioStatusCtrl_io_interrupt[2:0]              ), //o
    .io_clock                    (io_clock                                             ), //i
    .resetCtrl_systemReset       (resetCtrl_systemReset                                )  //i
  );
  Apb3Gpio_1_ system_gpio0Ctrl ( 
    .io_bus_PADDR                (_zz_60_[11:0]                                    ), //i
    .io_bus_PSEL                 (apb3Router_1__io_outputs_6_PSEL                  ), //i
    .io_bus_PENABLE              (apb3Router_1__io_outputs_6_PENABLE               ), //i
    .io_bus_PREADY               (system_gpio0Ctrl_io_bus_PREADY                   ), //o
    .io_bus_PWRITE               (apb3Router_1__io_outputs_6_PWRITE                ), //i
    .io_bus_PWDATA               (apb3Router_1__io_outputs_6_PWDATA[31:0]          ), //i
    .io_bus_PRDATA               (system_gpio0Ctrl_io_bus_PRDATA[31:0]             ), //o
    .io_bus_PSLVERROR            (system_gpio0Ctrl_io_bus_PSLVERROR                ), //o
    .io_gpio_pins_read           (io_gpio0_pins_read[11:0]                         ), //i
    .io_gpio_pins_write          (system_gpio0Ctrl_io_gpio_pins_write[11:0]        ), //o
    .io_gpio_pins_writeEnable    (system_gpio0Ctrl_io_gpio_pins_writeEnable[11:0]  ), //o
    .io_interrupt                (system_gpio0Ctrl_io_interrupt[11:0]              ), //o
    .io_clock                    (io_clock                                         ), //i
    .resetCtrl_systemReset       (resetCtrl_systemReset                            )  //i
  );
  Apb3SpiMaster system_spiMaster0Ctrl ( 
    .io_bus_PADDR             (_zz_61_[11:0]                              ), //i
    .io_bus_PSEL              (apb3Router_1__io_outputs_7_PSEL            ), //i
    .io_bus_PENABLE           (apb3Router_1__io_outputs_7_PENABLE         ), //i
    .io_bus_PREADY            (system_spiMaster0Ctrl_io_bus_PREADY        ), //o
    .io_bus_PWRITE            (apb3Router_1__io_outputs_7_PWRITE          ), //i
    .io_bus_PWDATA            (apb3Router_1__io_outputs_7_PWDATA[31:0]    ), //i
    .io_bus_PRDATA            (system_spiMaster0Ctrl_io_bus_PRDATA[31:0]  ), //o
    .io_bus_PSLVERROR         (system_spiMaster0Ctrl_io_bus_PSLVERROR     ), //o
    .io_spi_ss                (system_spiMaster0Ctrl_io_spi_ss            ), //o
    .io_spi_sclk              (system_spiMaster0Ctrl_io_spi_sclk          ), //o
    .io_spi_mosi              (system_spiMaster0Ctrl_io_spi_mosi          ), //o
    .io_spi_miso              (io_spi0_miso                               ), //i
    .io_interrupt             (system_spiMaster0Ctrl_io_interrupt         ), //o
    .io_clock                 (io_clock                                   ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                      )  //i
  );
  Apb3UniqueID system_uniqueID0Ctrl ( 
    .io_bus_PADDR             (_zz_62_[11:0]                             ), //i
    .io_bus_PSEL              (apb3Router_1__io_outputs_8_PSEL           ), //i
    .io_bus_PENABLE           (apb3Router_1__io_outputs_8_PENABLE        ), //i
    .io_bus_PREADY            (system_uniqueID0Ctrl_io_bus_PREADY        ), //o
    .io_bus_PWRITE            (apb3Router_1__io_outputs_8_PWRITE         ), //i
    .io_bus_PWDATA            (apb3Router_1__io_outputs_8_PWDATA[31:0]   ), //i
    .io_bus_PRDATA            (system_uniqueID0Ctrl_io_bus_PRDATA[31:0]  ), //o
    .io_bus_PSLVERROR         (system_uniqueID0Ctrl_io_bus_PSLVERROR     ), //o
    .io_clock                 (io_clock                                  ), //i
    .resetCtrl_systemReset    (resetCtrl_systemReset                     )  //i
  );
  Apb3Decoder io_apb_decoder ( 
    .io_input_PADDR         (system_apbBridge_io_apb_PADDR[19:0]    ), //i
    .io_input_PSEL          (system_apbBridge_io_apb_PSEL           ), //i
    .io_input_PENABLE       (system_apbBridge_io_apb_PENABLE        ), //i
    .io_input_PREADY        (io_apb_decoder_io_input_PREADY         ), //o
    .io_input_PWRITE        (system_apbBridge_io_apb_PWRITE         ), //i
    .io_input_PWDATA        (system_apbBridge_io_apb_PWDATA[31:0]   ), //i
    .io_input_PRDATA        (io_apb_decoder_io_input_PRDATA[31:0]   ), //o
    .io_input_PSLVERROR     (io_apb_decoder_io_input_PSLVERROR      ), //o
    .io_output_PADDR        (io_apb_decoder_io_output_PADDR[19:0]   ), //o
    .io_output_PSEL         (io_apb_decoder_io_output_PSEL[8:0]     ), //o
    .io_output_PENABLE      (io_apb_decoder_io_output_PENABLE       ), //o
    .io_output_PREADY       (apb3Router_1__io_input_PREADY          ), //i
    .io_output_PWRITE       (io_apb_decoder_io_output_PWRITE        ), //o
    .io_output_PWDATA       (io_apb_decoder_io_output_PWDATA[31:0]  ), //o
    .io_output_PRDATA       (apb3Router_1__io_input_PRDATA[31:0]    ), //i
    .io_output_PSLVERROR    (apb3Router_1__io_input_PSLVERROR       )  //i
  );
  Apb3Router apb3Router_1_ ( 
    .io_input_PADDR            (io_apb_decoder_io_output_PADDR[19:0]       ), //i
    .io_input_PSEL             (io_apb_decoder_io_output_PSEL[8:0]         ), //i
    .io_input_PENABLE          (io_apb_decoder_io_output_PENABLE           ), //i
    .io_input_PREADY           (apb3Router_1__io_input_PREADY              ), //o
    .io_input_PWRITE           (io_apb_decoder_io_output_PWRITE            ), //i
    .io_input_PWDATA           (io_apb_decoder_io_output_PWDATA[31:0]      ), //i
    .io_input_PRDATA           (apb3Router_1__io_input_PRDATA[31:0]        ), //o
    .io_input_PSLVERROR        (apb3Router_1__io_input_PSLVERROR           ), //o
    .io_outputs_0_PADDR        (apb3Router_1__io_outputs_0_PADDR[19:0]     ), //o
    .io_outputs_0_PSEL         (apb3Router_1__io_outputs_0_PSEL            ), //o
    .io_outputs_0_PENABLE      (apb3Router_1__io_outputs_0_PENABLE         ), //o
    .io_outputs_0_PREADY       (system_mtimerCtrl_io_bus_PREADY            ), //i
    .io_outputs_0_PWRITE       (apb3Router_1__io_outputs_0_PWRITE          ), //o
    .io_outputs_0_PWDATA       (apb3Router_1__io_outputs_0_PWDATA[31:0]    ), //o
    .io_outputs_0_PRDATA       (system_mtimerCtrl_io_bus_PRDATA[31:0]      ), //i
    .io_outputs_0_PSLVERROR    (system_mtimerCtrl_io_bus_PSLVERROR         ), //i
    .io_outputs_1_PADDR        (apb3Router_1__io_outputs_1_PADDR[19:0]     ), //o
    .io_outputs_1_PSEL         (apb3Router_1__io_outputs_1_PSEL            ), //o
    .io_outputs_1_PENABLE      (apb3Router_1__io_outputs_1_PENABLE         ), //o
    .io_outputs_1_PREADY       (system_plicCtrl_io_bus_PREADY              ), //i
    .io_outputs_1_PWRITE       (apb3Router_1__io_outputs_1_PWRITE          ), //o
    .io_outputs_1_PWDATA       (apb3Router_1__io_outputs_1_PWDATA[31:0]    ), //o
    .io_outputs_1_PRDATA       (system_plicCtrl_io_bus_PRDATA[31:0]        ), //i
    .io_outputs_1_PSLVERROR    (system_plicCtrl_io_bus_PSLVERROR           ), //i
    .io_outputs_2_PADDR        (apb3Router_1__io_outputs_2_PADDR[19:0]     ), //o
    .io_outputs_2_PSEL         (apb3Router_1__io_outputs_2_PSEL            ), //o
    .io_outputs_2_PENABLE      (apb3Router_1__io_outputs_2_PENABLE         ), //o
    .io_outputs_2_PREADY       (system_uartStdCtrl_io_bus_PREADY           ), //i
    .io_outputs_2_PWRITE       (apb3Router_1__io_outputs_2_PWRITE          ), //o
    .io_outputs_2_PWDATA       (apb3Router_1__io_outputs_2_PWDATA[31:0]    ), //o
    .io_outputs_2_PRDATA       (system_uartStdCtrl_io_bus_PRDATA[31:0]     ), //i
    .io_outputs_2_PSLVERROR    (system_uartStdCtrl_io_bus_PSLVERROR        ), //i
    .io_outputs_3_PADDR        (apb3Router_1__io_outputs_3_PADDR[19:0]     ), //o
    .io_outputs_3_PSEL         (apb3Router_1__io_outputs_3_PSEL            ), //o
    .io_outputs_3_PENABLE      (apb3Router_1__io_outputs_3_PENABLE         ), //o
    .io_outputs_3_PREADY       (system_uartComCtrl_io_bus_PREADY           ), //i
    .io_outputs_3_PWRITE       (apb3Router_1__io_outputs_3_PWRITE          ), //o
    .io_outputs_3_PWDATA       (apb3Router_1__io_outputs_3_PWDATA[31:0]    ), //o
    .io_outputs_3_PRDATA       (system_uartComCtrl_io_bus_PRDATA[31:0]     ), //i
    .io_outputs_3_PSLVERROR    (system_uartComCtrl_io_bus_PSLVERROR        ), //i
    .io_outputs_4_PADDR        (apb3Router_1__io_outputs_4_PADDR[19:0]     ), //o
    .io_outputs_4_PSEL         (apb3Router_1__io_outputs_4_PSEL            ), //o
    .io_outputs_4_PENABLE      (apb3Router_1__io_outputs_4_PENABLE         ), //o
    .io_outputs_4_PREADY       (system_uartRS232Ctrl_io_bus_PREADY         ), //i
    .io_outputs_4_PWRITE       (apb3Router_1__io_outputs_4_PWRITE          ), //o
    .io_outputs_4_PWDATA       (apb3Router_1__io_outputs_4_PWDATA[31:0]    ), //o
    .io_outputs_4_PRDATA       (system_uartRS232Ctrl_io_bus_PRDATA[31:0]   ), //i
    .io_outputs_4_PSLVERROR    (system_uartRS232Ctrl_io_bus_PSLVERROR      ), //i
    .io_outputs_5_PADDR        (apb3Router_1__io_outputs_5_PADDR[19:0]     ), //o
    .io_outputs_5_PSEL         (apb3Router_1__io_outputs_5_PSEL            ), //o
    .io_outputs_5_PENABLE      (apb3Router_1__io_outputs_5_PENABLE         ), //o
    .io_outputs_5_PREADY       (system_gpioStatusCtrl_io_bus_PREADY        ), //i
    .io_outputs_5_PWRITE       (apb3Router_1__io_outputs_5_PWRITE          ), //o
    .io_outputs_5_PWDATA       (apb3Router_1__io_outputs_5_PWDATA[31:0]    ), //o
    .io_outputs_5_PRDATA       (system_gpioStatusCtrl_io_bus_PRDATA[31:0]  ), //i
    .io_outputs_5_PSLVERROR    (system_gpioStatusCtrl_io_bus_PSLVERROR     ), //i
    .io_outputs_6_PADDR        (apb3Router_1__io_outputs_6_PADDR[19:0]     ), //o
    .io_outputs_6_PSEL         (apb3Router_1__io_outputs_6_PSEL            ), //o
    .io_outputs_6_PENABLE      (apb3Router_1__io_outputs_6_PENABLE         ), //o
    .io_outputs_6_PREADY       (system_gpio0Ctrl_io_bus_PREADY             ), //i
    .io_outputs_6_PWRITE       (apb3Router_1__io_outputs_6_PWRITE          ), //o
    .io_outputs_6_PWDATA       (apb3Router_1__io_outputs_6_PWDATA[31:0]    ), //o
    .io_outputs_6_PRDATA       (system_gpio0Ctrl_io_bus_PRDATA[31:0]       ), //i
    .io_outputs_6_PSLVERROR    (system_gpio0Ctrl_io_bus_PSLVERROR          ), //i
    .io_outputs_7_PADDR        (apb3Router_1__io_outputs_7_PADDR[19:0]     ), //o
    .io_outputs_7_PSEL         (apb3Router_1__io_outputs_7_PSEL            ), //o
    .io_outputs_7_PENABLE      (apb3Router_1__io_outputs_7_PENABLE         ), //o
    .io_outputs_7_PREADY       (system_spiMaster0Ctrl_io_bus_PREADY        ), //i
    .io_outputs_7_PWRITE       (apb3Router_1__io_outputs_7_PWRITE          ), //o
    .io_outputs_7_PWDATA       (apb3Router_1__io_outputs_7_PWDATA[31:0]    ), //o
    .io_outputs_7_PRDATA       (system_spiMaster0Ctrl_io_bus_PRDATA[31:0]  ), //i
    .io_outputs_7_PSLVERROR    (system_spiMaster0Ctrl_io_bus_PSLVERROR     ), //i
    .io_outputs_8_PADDR        (apb3Router_1__io_outputs_8_PADDR[19:0]     ), //o
    .io_outputs_8_PSEL         (apb3Router_1__io_outputs_8_PSEL            ), //o
    .io_outputs_8_PENABLE      (apb3Router_1__io_outputs_8_PENABLE         ), //o
    .io_outputs_8_PREADY       (system_uniqueID0Ctrl_io_bus_PREADY         ), //i
    .io_outputs_8_PWRITE       (apb3Router_1__io_outputs_8_PWRITE          ), //o
    .io_outputs_8_PWDATA       (apb3Router_1__io_outputs_8_PWDATA[31:0]    ), //o
    .io_outputs_8_PRDATA       (system_uniqueID0Ctrl_io_bus_PRDATA[31:0]   ), //i
    .io_outputs_8_PSLVERROR    (system_uniqueID0Ctrl_io_bus_PSLVERROR      ), //i
    .io_clock                  (io_clock                                   ), //i
    .resetCtrl_systemReset     (resetCtrl_systemReset                      )  //i
  );
  always @ (*) begin
    resetCtrl_mainClkResetUnbuffered = 1'b0;
    if(_zz_63_)begin
      resetCtrl_mainClkResetUnbuffered = 1'b1;
    end
  end

  assign _zz_1_[5 : 0] = 6'h3f;
  assign io_sysReset_out = resetCtrl_systemReset;
  assign _zz_24_ = (! (axi4ReadOnlyDecoder_1__io_input_r_payload_resp == (2'b00)));
  always @ (*) begin
    _zz_2_ = 1'b0;
    if(((system_core_cpu_dBus_cmd_valid && _zz_27_) && system_core_cpu_dBus_cmd_payload_wr))begin
      _zz_2_ = 1'b1;
    end
  end

  always @ (*) begin
    _zz_3_ = 1'b0;
    if((axi4SharedDecoder_1__io_input_b_valid && 1'b1))begin
      _zz_3_ = 1'b1;
    end
  end

  always @ (*) begin
    if((_zz_2_ && (! _zz_3_)))begin
      _zz_5_ = (3'b001);
    end else begin
      if(((! _zz_2_) && _zz_3_))begin
        _zz_5_ = (3'b111);
      end else begin
        _zz_5_ = (3'b000);
      end
    end
  end

  assign _zz_6_ = (! ((((_zz_4_ != (3'b000)) && system_core_cpu_dBus_cmd_valid) && (! system_core_cpu_dBus_cmd_payload_wr)) || (_zz_4_ == (3'b111))));
  assign _zz_27_ = (streamFork_3__io_input_ready && _zz_6_);
  assign _zz_29_ = (system_core_cpu_dBus_cmd_valid && _zz_6_);
  always @ (*) begin
    streamFork_3__io_outputs_1_thrown_valid = streamFork_3__io_outputs_1_valid;
    if(_zz_64_)begin
      streamFork_3__io_outputs_1_thrown_valid = 1'b0;
    end
  end

  always @ (*) begin
    _zz_30_ = streamFork_3__io_outputs_1_thrown_ready;
    if(_zz_64_)begin
      _zz_30_ = 1'b1;
    end
  end

  assign streamFork_3__io_outputs_1_thrown_payload_wr = streamFork_3__io_outputs_1_payload_wr;
  assign streamFork_3__io_outputs_1_thrown_payload_address = streamFork_3__io_outputs_1_payload_address;
  assign streamFork_3__io_outputs_1_thrown_payload_data = streamFork_3__io_outputs_1_payload_data;
  assign streamFork_3__io_outputs_1_thrown_payload_size = streamFork_3__io_outputs_1_payload_size;
  assign streamFork_3__io_outputs_1_thrown_ready = axi4SharedDecoder_1__io_input_w_ready;
  always @ (*) begin
    case(streamFork_3__io_outputs_1_thrown_payload_size)
      2'b00 : begin
        _zz_7_ = (4'b0001);
      end
      2'b01 : begin
        _zz_7_ = (4'b0011);
      end
      default : begin
        _zz_7_ = (4'b1111);
      end
    endcase
  end

  assign _zz_28_ = (! (axi4SharedDecoder_1__io_input_r_m2sPipe_payload_resp == (2'b00)));
  assign _zz_26_ = systemDebugger_1__io_mem_cmd_payload_address[7:0];
  assign io_jtag_tdo = jtagBridge_1__io_jtag_tdo;
  assign _zz_9_ = _zz_11_;
  assign _zz_35_ = (_zz_10_ && _zz_11_);
  assign _zz_10_ = system_onChipRam_io_axi_arbiter_io_readInputs_0_ar_ready;
  assign _zz_31_ = {system_core_cpu_iBus_cmd_payload_pc[31 : 2],(2'b00)};
  assign _zz_32_ = (4'b1111);
  assign _zz_33_ = (3'b110);
  assign _zz_34_ = 1'b1;
  assign _zz_12_ = _zz_14_;
  assign _zz_43_ = (_zz_13_ && _zz_14_);
  assign _zz_13_ = system_onChipRam_io_axi_arbiter_io_sharedInputs_0_arw_ready;
  assign _zz_15_ = _zz_17_;
  assign _zz_44_ = (_zz_16_ && _zz_17_);
  assign _zz_16_ = system_apbBridge_io_axi_arbiter_io_sharedInputs_0_arw_ready;
  assign _zz_36_ = {1'd0, streamFork_3__io_outputs_0_payload_size};
  assign _zz_37_ = (4'b1111);
  assign _zz_38_ = (3'b010);
  assign _zz_39_ = _zz_69_[3:0];
  assign _zz_40_ = 1'b1;
  assign _zz_41_ = 1'b1;
  assign _zz_42_ = ((1'b1 && (! axi4SharedDecoder_1__io_input_r_m2sPipe_valid)) || axi4SharedDecoder_1__io_input_r_m2sPipe_ready);
  assign axi4SharedDecoder_1__io_input_r_m2sPipe_valid = axi4SharedDecoder_1__io_input_r_m2sPipe_rValid;
  assign axi4SharedDecoder_1__io_input_r_m2sPipe_payload_data = axi4SharedDecoder_1__io_input_r_m2sPipe_rData_data;
  assign axi4SharedDecoder_1__io_input_r_m2sPipe_payload_resp = axi4SharedDecoder_1__io_input_r_m2sPipe_rData_resp;
  assign axi4SharedDecoder_1__io_input_r_m2sPipe_payload_last = axi4SharedDecoder_1__io_input_r_m2sPipe_rData_last;
  assign axi4SharedDecoder_1__io_input_r_m2sPipe_ready = 1'b1;
  assign _zz_45_ = axi4ReadOnlyDecoder_1__io_outputs_0_ar_payload_addr[16:0];
  assign _zz_18_[2 : 0] = (3'b000);
  assign _zz_19_[7 : 0] = 8'h0;
  assign _zz_46_ = (3'b010);
  assign _zz_47_ = (2'b01);
  assign _zz_48_ = axi4SharedDecoder_1__io_sharedOutputs_0_arw_payload_addr[16:0];
  assign _zz_20_[2 : 0] = (3'b000);
  assign _zz_21_[7 : 0] = 8'h0;
  assign _zz_49_ = (2'b01);
  assign system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_valid = system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_valid;
  assign system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_addr = system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_addr;
  assign system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_id = system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_id;
  assign system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_len = system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_len;
  assign system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_size = system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_size;
  assign system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_burst = system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_burst;
  assign system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_payload_write = system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_write;
  assign system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_ready = system_onChipRam_io_axi_arw_ready;
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_valid = (system_onChipRam_io_axi_arbiter_io_output_w_valid || system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rValid);
  assign _zz_50_ = (! system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rValid);
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_payload_data = (system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rValid ? system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rData_data : system_onChipRam_io_axi_arbiter_io_output_w_payload_data);
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_payload_strb = (system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rValid ? system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rData_strb : system_onChipRam_io_axi_arbiter_io_output_w_payload_strb);
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_payload_last = (system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rValid ? system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rData_last : system_onChipRam_io_axi_arbiter_io_output_w_payload_last);
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_ready = ((1'b1 && (! system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_valid)) || system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_ready);
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_valid = system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rValid;
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_payload_data = system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rData_data;
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_payload_strb = system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rData_strb;
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_payload_last = system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rData_last;
  assign system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_ready = system_onChipRam_io_axi_w_ready;
  assign _zz_51_ = axi4SharedDecoder_1__io_sharedOutputs_1_arw_payload_addr[19:0];
  assign _zz_22_[3 : 0] = (4'b0000);
  assign _zz_23_[7 : 0] = 8'h0;
  assign _zz_52_ = (2'b01);
  assign system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_valid = system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_valid;
  assign system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_addr = system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_addr;
  assign system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_id = system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_id;
  assign system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_len = system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_len;
  assign system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_size = system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_size;
  assign system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_burst = system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_burst;
  assign system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_payload_write = system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_write;
  assign system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_ready = system_apbBridge_io_axi_arw_ready;
  assign system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_valid = system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_valid;
  assign system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_payload_data = system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_payload_data;
  assign system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_payload_strb = system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_payload_strb;
  assign system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_payload_last = system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_payload_last;
  assign system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_ready = system_apbBridge_io_axi_w_ready;
  assign system_core_mtimerInterrupt = system_mtimerCtrl_io_interrupt;
  assign system_core_globalInterrupt = system_plicCtrl_io_interrupt;
  always @ (*) begin
    _zz_55_[0] = 1'b0;
    _zz_55_[1] = system_uartStdCtrl_io_interrupt;
    _zz_55_[2] = system_uartComCtrl_io_interrupt;
    _zz_55_[3] = system_uartRS232Ctrl_io_interrupt;
  end

  assign io_uartStd_txd = system_uartStdCtrl_io_uart_txd;
  assign io_uartCom_txd = system_uartComCtrl_io_uart_txd;
  assign io_uartRS232_txd = system_uartRS232Ctrl_io_uart_txd;
  assign io_gpioStatus_pins_write = system_gpioStatusCtrl_io_gpio_pins_write;
  assign io_gpioStatus_pins_writeEnable = system_gpioStatusCtrl_io_gpio_pins_writeEnable;
  assign io_gpio0_pins_write = system_gpio0Ctrl_io_gpio_pins_write;
  assign io_gpio0_pins_writeEnable = system_gpio0Ctrl_io_gpio_pins_writeEnable;
  assign io_spi0_ss = system_spiMaster0Ctrl_io_spi_ss;
  assign io_spi0_sclk = system_spiMaster0Ctrl_io_spi_sclk;
  assign io_spi0_mosi = system_spiMaster0Ctrl_io_spi_mosi;
  assign _zz_53_ = apb3Router_1__io_outputs_0_PADDR[11:0];
  assign _zz_54_ = apb3Router_1__io_outputs_1_PADDR[15:0];
  assign _zz_56_ = apb3Router_1__io_outputs_2_PADDR[11:0];
  assign _zz_57_ = apb3Router_1__io_outputs_3_PADDR[11:0];
  assign _zz_58_ = apb3Router_1__io_outputs_4_PADDR[11:0];
  assign _zz_59_ = apb3Router_1__io_outputs_5_PADDR[11:0];
  assign _zz_60_ = apb3Router_1__io_outputs_6_PADDR[11:0];
  assign _zz_61_ = apb3Router_1__io_outputs_7_PADDR[11:0];
  assign _zz_62_ = apb3Router_1__io_outputs_8_PADDR[11:0];
  assign _zz_25_ = 1'b0;
  always @ (posedge io_clock) begin
    if(_zz_63_)begin
      resetCtrl_systemClkResetCounter <= (resetCtrl_systemClkResetCounter + 6'h01);
    end
    if(io_reset_buffercc_io_dataOut)begin
      resetCtrl_systemClkResetCounter <= 6'h0;
    end
  end

  always @ (posedge io_clock) begin
    resetCtrl_systemReset <= resetCtrl_mainClkResetUnbuffered;
    resetCtrl_debugReset <= resetCtrl_mainClkResetUnbuffered;
    if(system_core_cpu_debug_resetOut_regNext)begin
      resetCtrl_systemReset <= 1'b1;
    end
  end

  always @ (posedge io_clock or posedge resetCtrl_systemReset) begin
    if (resetCtrl_systemReset) begin
      _zz_4_ <= (3'b000);
      _zz_11_ <= 1'b0;
      _zz_14_ <= 1'b0;
      _zz_17_ <= 1'b0;
      axi4SharedDecoder_1__io_input_r_m2sPipe_rValid <= 1'b0;
      system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_valid <= 1'b0;
      system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_ready <= 1'b1;
      system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rValid <= 1'b0;
      system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rValid <= 1'b0;
      system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_valid <= 1'b0;
      system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_ready <= 1'b1;
      system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_valid <= 1'b0;
      system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_ready <= 1'b1;
    end else begin
      _zz_4_ <= (_zz_4_ + _zz_5_);
      if(axi4ReadOnlyDecoder_1__io_outputs_0_ar_valid)begin
        _zz_11_ <= 1'b1;
      end
      if((_zz_9_ && _zz_10_))begin
        _zz_11_ <= 1'b0;
      end
      if(axi4SharedDecoder_1__io_sharedOutputs_0_arw_valid)begin
        _zz_14_ <= 1'b1;
      end
      if((_zz_12_ && _zz_13_))begin
        _zz_14_ <= 1'b0;
      end
      if(axi4SharedDecoder_1__io_sharedOutputs_1_arw_valid)begin
        _zz_17_ <= 1'b1;
      end
      if((_zz_15_ && _zz_16_))begin
        _zz_17_ <= 1'b0;
      end
      if(_zz_42_)begin
        axi4SharedDecoder_1__io_input_r_m2sPipe_rValid <= axi4SharedDecoder_1__io_input_r_valid;
      end
      if(_zz_65_)begin
        system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_valid <= system_onChipRam_io_axi_arbiter_io_output_arw_valid;
        system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_ready <= (! system_onChipRam_io_axi_arbiter_io_output_arw_valid);
      end else begin
        system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_valid <= (! system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_ready);
        system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_ready <= system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_ready;
      end
      if(system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_ready)begin
        system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rValid <= 1'b0;
      end
      if(_zz_66_)begin
        system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rValid <= system_onChipRam_io_axi_arbiter_io_output_w_valid;
      end
      if(system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_ready)begin
        system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rValid <= system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_valid;
      end
      if(_zz_67_)begin
        system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_valid <= system_apbBridge_io_axi_arbiter_io_output_arw_valid;
        system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_ready <= (! system_apbBridge_io_axi_arbiter_io_output_arw_valid);
      end else begin
        system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_valid <= (! system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_ready);
        system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_ready <= system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_ready;
      end
      if(_zz_68_)begin
        system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_valid <= system_apbBridge_io_axi_arbiter_io_output_w_valid;
        system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_ready <= (! system_apbBridge_io_axi_arbiter_io_output_w_valid);
      end else begin
        system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_valid <= (! system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_ready);
        system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_ready <= system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_ready;
      end
    end
  end

  always @ (posedge io_clock) begin
    system_core_cpu_debug_resetOut_regNext <= system_core_cpu_debug_resetOut;
  end

  always @ (posedge io_clock or posedge resetCtrl_debugReset) begin
    if (resetCtrl_debugReset) begin
      _zz_8_ <= 1'b0;
    end else begin
      _zz_8_ <= (systemDebugger_1__io_mem_cmd_valid && system_core_cpu_debug_bus_cmd_ready);
    end
  end

  always @ (posedge io_clock) begin
    if(_zz_42_)begin
      axi4SharedDecoder_1__io_input_r_m2sPipe_rData_data <= axi4SharedDecoder_1__io_input_r_payload_data;
      axi4SharedDecoder_1__io_input_r_m2sPipe_rData_resp <= axi4SharedDecoder_1__io_input_r_payload_resp;
      axi4SharedDecoder_1__io_input_r_m2sPipe_rData_last <= axi4SharedDecoder_1__io_input_r_payload_last;
    end
    if(_zz_65_)begin
      system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_addr <= system_onChipRam_io_axi_arbiter_io_output_arw_payload_addr;
      system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_id <= system_onChipRam_io_axi_arbiter_io_output_arw_payload_id;
      system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_len <= system_onChipRam_io_axi_arbiter_io_output_arw_payload_len;
      system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_size <= system_onChipRam_io_axi_arbiter_io_output_arw_payload_size;
      system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_burst <= system_onChipRam_io_axi_arbiter_io_output_arw_payload_burst;
      system_onChipRam_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_write <= system_onChipRam_io_axi_arbiter_io_output_arw_payload_write;
    end
    if(_zz_66_)begin
      system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rData_data <= system_onChipRam_io_axi_arbiter_io_output_w_payload_data;
      system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rData_strb <= system_onChipRam_io_axi_arbiter_io_output_w_payload_strb;
      system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_rData_last <= system_onChipRam_io_axi_arbiter_io_output_w_payload_last;
    end
    if(system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_ready)begin
      system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rData_data <= system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_payload_data;
      system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rData_strb <= system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_payload_strb;
      system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_m2sPipe_rData_last <= system_onChipRam_io_axi_arbiter_io_output_w_s2mPipe_payload_last;
    end
    if(_zz_67_)begin
      system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_addr <= system_apbBridge_io_axi_arbiter_io_output_arw_payload_addr;
      system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_id <= system_apbBridge_io_axi_arbiter_io_output_arw_payload_id;
      system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_len <= system_apbBridge_io_axi_arbiter_io_output_arw_payload_len;
      system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_size <= system_apbBridge_io_axi_arbiter_io_output_arw_payload_size;
      system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_burst <= system_apbBridge_io_axi_arbiter_io_output_arw_payload_burst;
      system_apbBridge_io_axi_arbiter_io_output_arw_halfPipe_regs_payload_write <= system_apbBridge_io_axi_arbiter_io_output_arw_payload_write;
    end
    if(_zz_68_)begin
      system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_payload_data <= system_apbBridge_io_axi_arbiter_io_output_w_payload_data;
      system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_payload_strb <= system_apbBridge_io_axi_arbiter_io_output_w_payload_strb;
      system_apbBridge_io_axi_arbiter_io_output_w_halfPipe_regs_payload_last <= system_apbBridge_io_axi_arbiter_io_output_w_payload_last;
    end
  end


endmodule
